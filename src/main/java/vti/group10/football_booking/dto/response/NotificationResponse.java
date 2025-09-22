package vti.group10.football_booking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private Integer id;
    private String message;
    private String status;
    private String createdAt;
    private Integer userId;
    private String userFullName;
}
