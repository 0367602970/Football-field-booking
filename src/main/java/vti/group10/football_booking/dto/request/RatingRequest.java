package vti.group10.football_booking.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingRequest {
    private int fieldClusterId;
    private int score;
    private String comment;
}
