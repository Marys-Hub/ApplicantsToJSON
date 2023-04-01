import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ApplicantRanker {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final double BONUS_POINT = 1.0;
    private static final double MALUS_POINT = -1.0;
    //assuming the maximum for a better evaluation of the score
    private static final int MAX_SCORE = 100;

    /**

     The ApplicantRanker class represents a system for ranking applicants based on their application scores and delivery dates.
     It contains a nested Applicant class that represents an individual applicant, as well as methods for calculating adjusted scores
     and creating an ApplicantRanker object from a list of applicants or a single applicant's name and score.
     */

    public ApplicantRanker(List<Applicant> applicants) {

    }

    public ApplicantRanker(Serializable applicantName, double score) {

    }


    static class Applicant {
        private String name;
        private String email;
        private LocalDateTime deliveryDateTime;
        private double score;

        public Applicant(String name, String email, LocalDateTime deliveryDateTime, double score) {
            this.name = name;
            this.email = email;
            this.deliveryDateTime = deliveryDateTime;
            this.score = score;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public LocalDateTime getDeliveryDateTime() {
            return deliveryDateTime;
        }

        public double getScore() {
            return score;
        }

        /**

         Calculates the adjusted score for an applicant based on the delivery date of their application.
         The adjusted score is the original score with a bonus or a malus added depending on the delivery date.
         @param firstDay The first day of the application period.
         @param lastDay The last day of the application period.
         @return The adjusted score for the applicant.
         */
        public double getAdjustedScore(LocalDateTime firstDay, LocalDateTime lastDay) {
            double adjustedScore = score;
            if (deliveryDateTime.toLocalDate().equals(firstDay.toLocalDate())) {
                adjustedScore += BONUS_POINT;
            } else if (deliveryDateTime.isAfter(lastDay.withHour(12))) {
                adjustedScore += MALUS_POINT;
            }
            return Math.max(0, Math.min(MAX_SCORE, adjustedScore));
        }

    }

}
