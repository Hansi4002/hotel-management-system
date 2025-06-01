package lk.ijse.hotelmanagementsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FeedbackDTO {
    private String feedbackId;
    private String guestId;
    private Date submissionDate;
    private String comments;
    private int rating;

//    public void FeedbackDTO(String feedbackId, String guestId, Date submissionDate, String comments, int rating) {
//        this.feedbackId = feedbackId;
//        this.guestId = guestId;
//        this.submissionDate = submissionDate;
//        this.comments = comments;
//        this.rating = rating;
//    }
//
//    public String getFeedbackId() {return feedbackId;}
//    public String getGuestId() {return guestId;}
//    public Date getSubmissionDate() {return submissionDate;}
//    public String getComments() {return comments;}
//    public int getRating() {return rating;}
//
//    public void setFeedbackId(String feedbackId) {this.feedbackId = feedbackId;}
//    public void setGuestId(String guestId) {this.guestId = guestId;}
//    public void setSubmissionDate(Date submissionDate) {this.submissionDate = submissionDate;}
//    public void setComments(String comments) {this.comments = comments;}
//    public void setRating(int rating) {this.rating = rating;}
}



