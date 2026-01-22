package vti.group10.football_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldDTO {

        private Integer id;
        private String name; // Chỉ lấy tên sân

}
