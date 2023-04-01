import com.fasterxml.jackson.databind.MappingIterator;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.*;

public class ApplicantsProcessor {

    /**

     This function reads a CSV file from an InputStream and converts it to a list of maps representing each row in the CSV file.
     The first row of the CSV file is assumed to be the header and is used as the keys for the map.
     @param csvStream An input stream allowing to read the CSV input file.
     @return A list of maps representing each row in the CSV file.
     @throws IOException if the CSV file cannot be read or parsed.
     */
    public static List<Map<?, ?>> readCsvToJson(InputStream csvStream) {
        List<Map<?, ?>> list = new ArrayList<>();
        File input = new File("applicants.csv");
        try {
            CsvSchema csv = CsvSchema.emptySchema().withHeader();
            CsvMapper csvMapper = new CsvMapper();
            MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(input);
            list = mappingIterator.readAll();
            System.out.println(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void writeCsvToJsonFile() {
        try {
            InputStream csvStream = new FileInputStream("src/applicants.csv");
            List<Map<?, ?>> jsonData = readCsvToJson(csvStream);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File("applicants.json"), jsonData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Processes a CSV file of applicants and returns a JSON string that includes the following information:
     * The number of unique applicants
     * The top n applicants based on their score
     * The average score of the top half of applicants
     * The CSV file may or may not have a header row, which is handled accordingly by the function.
     * @param csvStream The input stream of the CSV file to be processed
     * @return A JSON string that includes the processed information or null if an error occurs during processing
     */
    public String processApplicants(InputStream csvStream) {
        try {
            List<Map<?, ?>> csvLines = readCsvToJson(csvStream);

            CsvMapper csvMapper = new CsvMapper();
            CsvSchema csvSchema = csvMapper.schemaFor(ApplicantRanker.Applicant.class).withHeader();

            //Checking whether the csvLines list is empty to determine whether the original input stream
            // needs to be used as the input for the MappingIterator or whether the header from the
            // first line of csvLines should be used instead.

            MappingIterator<ApplicantRanker.Applicant> mappingIterator = csvMapper.
                    readerFor(ApplicantRanker.Applicant.class)
                    .with(csvSchema).
                    readValues(csvLines.isEmpty() ? csvStream : new ByteArrayInputStream(csvLines.get(0).keySet()
                            .toArray()[0].toString().getBytes()));
            List<ApplicantRanker.Applicant> applicants = mappingIterator.readAll();

            int uniqueApplicants = countUniqueApplicants(applicants);
            List<String> topApplicants = getTopApplicants(applicants, 3);
            double topHalfAverageScore = getTopHalfAverageScore(applicants);

            Map<String, Object> jsonResponse = new HashMap<>();
            jsonResponse.put("uniqueApplicants", uniqueApplicants);
            jsonResponse.put("topApplicants", topApplicants);
            jsonResponse.put("averageScore", topHalfAverageScore);

            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**

     Calculates the average score of the top half of applicants.
     @param applicants list of Applicant objects
     @return the average score of the top half of applicants
     */
    private double getTopHalfAverageScore(List<ApplicantRanker.Applicant> applicants) {
        int halfSize = (int) Math.ceil((double) applicants.size() / 2);
        List<ApplicantRanker.Applicant> topHalf = applicants.stream()
                .sorted(Comparator.comparing(ApplicantRanker.Applicant::getScore).reversed()
                        .thenComparing(ApplicantRanker.Applicant::getDeliveryDateTime)
                        .thenComparing(ApplicantRanker.Applicant::getEmail))
                .limit(halfSize)
                .collect(Collectors.toList());

        double sum = topHalf.stream().mapToDouble(ApplicantRanker.Applicant::getScore).sum();
        return sum / topHalf.size();
    }

    /**

     Counts the number of unique applicants in the list of applicants.
     @param applicants a list of Applicant objects
     @return the number of unique applicants based on their email addresses
     */
    private int countUniqueApplicants(List<ApplicantRanker.Applicant> applicants) {
        Set<String> uniqueEmails = new HashSet<>();
        for (ApplicantRanker.Applicant applicant : applicants) {
            uniqueEmails.add(applicant.getEmail());
        }
        return uniqueEmails.size();
    }



    /**
     * Returns the names of the top n applicants with the highest scores, as determined by the sum of their adjusted scores.
     * If there are less than n applicants, all applicants will be returned.
     *
     * @param applicants The list of applicants to process.
     * @param numTop The maximum number of top applicants to return.
     * @return A list of names of the top n applicants, sorted by descending adjusted score.
     */


    private List<String> getTopApplicants(List<ApplicantRanker.Applicant> applicants, int numTop) {
        List<ApplicantRanker> applicantRankers = new ArrayList<>();
        for (ApplicantRanker.Applicant applicant : applicants) {
            double score = applicant.getAdjustedScore();
            applicantRankers.add(new ApplicantRanker(applicant.getName(), score));
        }
        Collections.sort(applicantRankers);

        List<String> topApplicants = new ArrayList<>();
        for (int i = 0; i < Math.min(numTop, applicantRankers.size()); i++) {
            topApplicants.add(applicantRankers.get(i).getName());
        }
        return topApplicants;
    }



}
