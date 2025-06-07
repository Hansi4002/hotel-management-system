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
}



