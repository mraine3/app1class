package com.lessons.workers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.lessons.services.ElasticSearchService;
import com.lessons.services.JobService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.*;


public class FileWorker implements Callable<String> {
    private static final Logger logger = LoggerFactory.getLogger(FileWorker.class);

    private final Integer              jobId;                       // Holds the jobId for this job
    private final JobService           jobService;                  // Holds a reference to the JobService so we can use it here
    private final ElasticSearchService elasticSearchService;
    private final String               indexName;                   // Holds the destination ES index name
    private final ObjectMapper         objectMapper;                // Used to write java objects to JSON
    private final File                 tempSourceFile;


    /**
     * FileWorker constructor.  This is used to pass-in Spring services and other dependencies
     * @param aJobId holds the ID that uniquely identifies this job
     * @param aInputStream Holds the inputStream corresponding to the uploaded file
     * @param aJobService Holds a reference to the JobService (which is used to upload the job status)
     */
    public FileWorker(Integer aJobId, String aIndexName, InputStream aInputStream, JobService aJobService, ElasticSearchService aElasticSearchService) throws Exception {
        this.jobId = aJobId;
        this.indexName = aIndexName;
        this.jobService = aJobService;
        this.elasticSearchService = aElasticSearchService;

        if (aInputStream == null) {
            throw new RuntimeException("Error in FileWorker constructor.  The passed-in aInputStreamExcelFile is null.");
        }

        // Create a tempFile from the inputStream (so the web app uses less memory)
        this.tempSourceFile = File.createTempFile("upload.report.", ".tmp");
        FileUtils.copyInputStreamToFile(aInputStream, this.tempSourceFile);
        logger.debug("Created this temp file: {}", this.tempSourceFile);




        // Initialize the objectMapper  (used to convert a single row into a JSON string)
        this.objectMapper = new ObjectMapper();

        // Tell the object mapper to convert Objects to snake case
        // For example.  object.getPersonName --> "person_name" in the json
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        // Escape non-nulls
        this.objectMapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
    }


    /**
     * Remove any temporary files
     */
    private void cleanup() {
        if (tempSourceFile != null) {
            logger.debug("Started Deleting temp file {}", tempSourceFile);
            boolean deleteWorked = tempSourceFile.delete();
            if (!deleteWorked) {
                logger.warn("WARNING:  Failed to delete this tempFile: {}", tempSourceFile);
            }
            else {
                logger.debug("Successfully deleted this tempFile: {}", tempSourceFile);
            }
        }
    }


    /**
     * Launch lots of PartialFileWorker threads to process this file in parallel
     *
     * @return a string that is not used
     */
    @Override
    public String call()  {
        long startTime = System.currentTimeMillis();
        logger.debug("FileWorker.call() started for jobId={}", this.jobId);

        // Set the job status as work-in-progress (1%)
        this.jobService.updateJobRecord(this.jobId, JobService.JOB_STATE_WORK_IN_PROGRESS, 1);

        try {
            logger.debug("call() started for jobId={}", this.jobId);

            // Launch multiple threads to parse each tab of the workbook in parallel
            processFileWithMultipleThreads();

            // Mark this job as finished successfully (so the front-end will know to stop polling)
            this.jobService.updateJobRecordAsSucceeded(this.jobId);

            logger.debug("call() finished for jobId={}", this.jobId);
        }
        catch (Exception e) {
            // This job failed
            logger.error("Error in FileWorker.call()", e);

            // Mark this job as finished with failure  (so the front-end will know to stop polling)
            this.jobService.updateJobRecordAsFailed(this.jobId, e.getMessage());
        }
        finally {
            cleanup();
        }

        long endTime = System.currentTimeMillis();
        logger.debug("FileWorker.call() finished for jobId={} in {} secs", this.jobId,  ((endTime - startTime) / 1000) );

        return null;
    }



    private void processFileWithMultipleThreads() throws Exception {

        final int TOTAL_ACTIVE_THREAD_COUNT = 100;

        // Create a threadpool with a fixed number of threads operating off a shared unbounded queue.
        ExecutorService executorService = Executors.newFixedThreadPool(TOTAL_ACTIVE_THREAD_COUNT);
        ExecutorCompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);


        try {

            final int TOTAL_RECORDS_PER_THREAD = 10000;

            int totalRecordsInFileContents = 0;
            StringBuilder fileContentsForThread = new StringBuilder();
            int totalThreadsLaunched = 0;

            // Read the temporary file one line at a time.  And, create threads as we go along
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.tempSourceFile))) {
                String csvLine;

                // Read the header line  (each chunk needs a header)
                String headerLine = bufferedReader.readLine();
                fileContentsForThread.append(headerLine).append("\n");

                while ((csvLine = bufferedReader.readLine()) != null) {

                    // Trim any leading/trailing spaces
                    csvLine = csvLine.trim();

                    if (StringUtils.isBlank(csvLine)) {
                        // Skip this empty line
                        continue;
                    }

                    // Append this line to the fileContents StringBuilder object
                    fileContentsForThread.append(csvLine).append("\n");
                    totalRecordsInFileContents++;

                    if (totalRecordsInFileContents == TOTAL_RECORDS_PER_THREAD) {
                        // Create a partial file worker to process this chunk of a file
                        totalThreadsLaunched++;
                        logger.debug("Submitting thread number {} to process {} lines", totalThreadsLaunched, totalRecordsInFileContents);
                        PartialFileWorker partialFileWorker = new PartialFileWorker(fileContentsForThread.toString(),  this.indexName, this.elasticSearchService, this.objectMapper);

                        // Submit the partial file worker to the queue
                        // NOTE:  If the queue is full, then this will BLOCK and wait for a spot to open
                        completionService.submit(partialFileWorker);

                        // Clear the fileContents StringBuilder object
                        fileContentsForThread.setLength(0);
                        totalRecordsInFileContents = 0;
                        fileContentsForThread.append(headerLine).append("\n");
                    }
                } // End of looping through rows in the file

                if (totalRecordsInFileContents > 0) {
                    // Submit a new worker to process this last piece
                    totalThreadsLaunched++;
                    logger.debug("Submitting thread number {} to process {} lines", totalThreadsLaunched, totalRecordsInFileContents);
                    PartialFileWorker partialFileWorker = new PartialFileWorker(fileContentsForThread.toString(),  this.indexName, this.elasticSearchService, this.objectMapper);

                    // Submit the partial file worker to the queue
                    // NOTE:  If the queue is full, then this will BLOCK and wait for a spot to open
                    completionService.submit(partialFileWorker);
                }


            } // End of try-with-resources block


            // W A I T      F O R      A L L      T H R E A D S    T O     F I N I S H
            final int totalThreads = totalThreadsLaunched;
            int totalFailedThreads = 0;
            int totalSuccessfulThreads = 0;
            for (int threadNumber=1; threadNumber <= totalThreads; threadNumber++)
            {
                try
                {
                    // Block and Wait for one of the threads to finish
                    Future<Boolean> f = completionService.take();

                    // One of the threads has completed
                    Boolean threadSucceeded = f.get();

                    if (! threadSucceeded) {
                        this.jobService.updateJobRecordAsFailed(this.jobId, "One of the threads failed");

                        // Stop here
                        return;
                    }

                    totalSuccessfulThreads++;
                    logger.debug("One thread came back.  totalSuccessfulThreads={}", totalSuccessfulThreads);

                    // One of the threads has finished successfully.  So, calculate total percent completed
                    float totalProgressAsFloat =  ( (float) totalSuccessfulThreads / totalThreads) * 100;
                    int totalProgressAsInt = (int) totalProgressAsFloat;

                    // Update the job record with the new total percent completed
                    this.jobService.updateJobRecord(this.jobId, JobService.JOB_STATE_WORK_IN_PROGRESS, totalProgressAsInt);
                }
                catch (Exception e)
                {
                    // One of the threads failed
                    logger.error("One thread failed totalFailedThreads=" + totalFailedThreads, e);

                    // One of the threads failed
                    this.jobService.updateJobRecordAsFailed(this.jobId, e.getMessage());

                    // Stop here
                    return;
                }
            } // end of for loop

        }
        finally {
            // Gracefully shutdown the executor service
            // NOTE:  Any running threads will run to completion first
            logger.debug("Shutting down executor service started");
            executorService.shutdown();

            // Wait for all of the threads to finish
            executorService.awaitTermination(10, TimeUnit.MINUTES);
            logger.debug("Shutting down executor service finished.");
        }

    }
}
