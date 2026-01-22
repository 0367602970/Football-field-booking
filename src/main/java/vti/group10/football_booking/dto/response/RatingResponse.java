package vti.group10.football_booking.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RatingResponse {
    private int id;
    private int score;
    private String comment;
    private int userId;
    private String userName;
    private int fieldClusterId;
    private LocalDateTime updatedAt;
}

