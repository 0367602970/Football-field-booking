package vti.group10.football_booking.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FieldRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String district;

    @NotBlank
    private String city;

    private String description;

    @NotNull
    private Double pricePerHour;

    private int typeId;

    private String status;

    private Double latitude;

    private Double longitude;

    private Integer ownerId;
//    private List<String> images;
}
