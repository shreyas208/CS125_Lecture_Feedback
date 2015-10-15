package com.shreyas208.cs125lecture;

import java.sql.Timestamp;

/**
 * Created by shreyas on 15-09-26.
 */
public class Submission {
    private Timestamp timestamp;
    private String userNetID;
    private String partnerNetID;
    private int lectureRating;
    private String feedbackGood;
    private String feedbackStruggling;

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getUserNetID() { return userNetID; }
    public void setUserNetID(String userNetID) { this.userNetID = userNetID; }

    public String getPartnerNetID() { return partnerNetID; }
    public void setPartnerNetID(String partnerNetID) { this.partnerNetID = partnerNetID; }

    public int getLectureRating() { return lectureRating; }
    public void setLectureRating(int lectureRating) { this.lectureRating = lectureRating; }

    public String getFeedbackGood() { return feedbackGood; }
    public void setFeedbackGood(String feedbackGood) { this.feedbackGood = feedbackGood; }

    public String getFeedbackStruggling() { return feedbackStruggling; }
    public void setFeedbackStruggling(String feedbackStruggling) { this.feedbackStruggling = feedbackStruggling; }
}