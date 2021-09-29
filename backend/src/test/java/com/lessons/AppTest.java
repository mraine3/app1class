package com.lessons;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Unit test for simple App.
 */
public class AppTest
{
    private static final Logger logger = LoggerFactory.getLogger(AppTest.class);
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final List<String> randomDescriptions = Arrays.asList("This is description of this silly report",
            "I am still working on this report",
            "This report might contain malware so please be careful",
            "The O'reilly group is still investigating this for CUI",
            "Unknown Report",
            "The ark is a source of unspeakable power and it has to be researched.",
            "\"And, it will be researched.  I assure you, Dr. Jones.  We have top men working on it now.  Top men.\"");


    private final List<String> randomPriorities = Arrays.asList("Low", "Medium", "High", "Critical");

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }



    @Ignore
    @Test
    public void createTestFile() throws Exception {
        long startTime = System.currentTimeMillis();

        final int TOTAL_RECORDS=1_000_000;
        final int tenPercentOfTotal = TOTAL_RECORDS / 10;

        File outputFile = new File("../docs/large_file." + TOTAL_RECORDS + ".csv");
        logger.debug("createTestFile() started.  Creating a file that holds {} records:\n\t{}", TOTAL_RECORDS, outputFile.getAbsolutePath());

        if (outputFile.exists() ) {
            outputFile.delete();
        }

        // Create a StringBuilder capable of holding 100 million bytes
        StringBuilder fileContents = new StringBuilder(100_000_000);

        // Add the header
        fileContents.append("id,display_name,description,priority,start_date,end_date\n");

        Date startDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("2015-01-01");
        Date startDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("2019-12-31");

        Date endDate1 = new SimpleDateFormat("yyyy-MM-dd").parse("2020-01-01");
        Date endDate2 = new SimpleDateFormat("yyyy-MM-dd").parse("2021-12-31");

        for (int i=1; i<=TOTAL_RECORDS; i++)
        {
            // Append one line to the stringBuilderobject
            fileContents.append(i)
                    .append(",")
                    .append("report ")
                    .append(i)
                    .append(",")
                    .append( getRandomDescription() )
                    .append(",")
                    .append( getRandomPriorityString() )
                    .append(",")
                    .append(getRandomDateBetweenDates(startDate1, startDate2) )
                    .append(",")
                    .append(getRandomDateBetweenDates(endDate1, endDate2))
                    .append("\n");


            if ((i % tenPercentOfTotal) == 0) {
                logger.debug("Finished row " + i);
            }
        }

        // Write the string to a file
        FileUtils.writeStringToFile(outputFile, fileContents.toString(), StandardCharsets.UTF_8);

        long endTime = System.currentTimeMillis();
        logger.debug("createTestFile() finished after {} secs", (endTime - startTime) / 1000);
    }


    private String getRandomPriorityString() {
        int randomPriorityIndex = ThreadLocalRandom.current().nextInt(0, this.randomPriorities.size());
        String rndPriorityString = this.randomPriorities.get(randomPriorityIndex);
        return rndPriorityString;
    }


    private String getRandomDescription() {
        int randomArrayIndex = ThreadLocalRandom.current().nextInt(0, this.randomDescriptions.size());
        String rndDescription = this.randomDescriptions.get(randomArrayIndex);
        return rndDescription;
    }


    private String getRandomDateBetweenDates(Date aStartDate, Date aEndDate) {
        Date randomDate = new Date(ThreadLocalRandom.current()
                .nextLong(aStartDate.getTime(), aEndDate.getTime()));

        return simpleDateFormat.format(randomDate);
    }




}
