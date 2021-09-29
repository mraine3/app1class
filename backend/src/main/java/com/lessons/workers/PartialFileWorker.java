package com.lessons.workers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lessons.models.ReportRecordDTO;
import com.lessons.services.ElasticSearchService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringReader;
import java.util.concurrent.Callable;

public class PartialFileWorker implements Callable<Boolean> {
    private static final Logger logger = LoggerFactory.getLogger(PartialFileWorker.class);

    private final ElasticSearchService elasticSearchService;
    private final ObjectMapper objectMapper;
    private final String fileContents;
    private final String indexName;


    public PartialFileWorker(String aFileContents, String aIndexName, ElasticSearchService aElasticSearchService, ObjectMapper aObjectMapper) {
        this.fileContents = aFileContents;
        this.indexName = aIndexName;
        this.elasticSearchService = aElasticSearchService;
        this.objectMapper = aObjectMapper;
    }

    @Override
    public Boolean call() throws Exception {
        final int BULK_RECORD_SIZE = 5000;
        int recordNumber = 0;

        CSVParser csvParser = null;
        String jsonForThisCsvLine;
        StringBuilder bulkJsonRequest = new StringBuilder(20000);
        StringReader stringReader = new StringReader(this.fileContents);

        try {
            csvParser = new CSVParser(stringReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .withQuote('"'));

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            // Loop through all csv lines in the fileContents string
            for (CSVRecord csvRecord : csvRecords) {

                // Create a ReportRecord object
                ReportRecordDTO reportRecordDTO = new ReportRecordDTO(
                        Integer.parseInt(csvRecord.get("id")),
                        csvRecord.get("description"),
                        csvRecord.get("display_name"),
                        csvRecord.get("priority")  );

                // Convert the ReportRecordDTO to a JSON string
                jsonForThisCsvLine = objectMapper.writeValueAsString(reportRecordDTO);


                // Append this JSON string to the bulkdJsonRequest
                bulkJsonRequest.append("{ \"index\": { \"_index\": \"").append(this.indexName).append("\" }}\n")
                        .append(jsonForThisCsvLine)
                        .append("\n");

                recordNumber++;

                if ((recordNumber % BULK_RECORD_SIZE) == 0) {
                    logger.debug("Sending a batch of records to ES {}", this.indexName);
                    // I've reached N records.  So, send this bulk request to ElasticSearch
                    this.elasticSearchService.bulkUpdate(bulkJsonRequest.toString(), true);

                    // Clear out the string builder object
                    recordNumber = 0;
                    bulkJsonRequest.setLength(0);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in PartialFileWorker.call()", e);

            // This thread failed, so return false;
            return false;
        }
        finally {
            if (stringReader != null) {
                stringReader.close();
            }

            if (csvParser != null) {
                csvParser.close();
            }
        }

        if (recordNumber > 0) {
            // Send the last partial JSON to ElasticSearch
            logger.debug("Sending the last batch of records to ES {}.", this.indexName);
            this.elasticSearchService.bulkUpdate(bulkJsonRequest.toString(), true);
        }

        // This thread succeeded, so return true;
        return true;
    }
}
