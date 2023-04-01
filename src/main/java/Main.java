import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;



public class Main {
    public static void main(String[] args) {

        ApplicantsProcessor processor = new ApplicantsProcessor();
        InputStream csvStream = null;
        try {
            csvStream = new FileInputStream("src/applicants.csv");
            ApplicantsProcessor.readCsvToJson(csvStream);
            ApplicantsProcessor.writeCsvToJsonFile();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csvStream != null) {
                try {
                    csvStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    static class ApplicantsProcessorTest {
        @Test
        void testProcessApplicants() {
            // Set up input stream
            String csv = "name,email,score\nJohn Doe,jdoe@example.com,10\nJane Doe,jane.doe@example.com,8\n";
            InputStream inputStream = new ByteArrayInputStream(csv.getBytes(StandardCharsets.UTF_8));

            // Test processApplicants() method
            ApplicantsProcessor ap = new ApplicantsProcessor();
            String result = ap.processApplicants(inputStream);

            // Check result
            String expected = "{\"uniqueApplicants\":2,\"topApplicants\":[\"John Doe\",\"Jane Doe\"],\"averageScore\":9.0}";
            assertEquals(expected, result);
        }

    }
}
