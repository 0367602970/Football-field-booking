package vti.group10.football_booking.dto;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;  // "success" | "error"
    private int code;       // HTTP status code
    private String message; 
    private T data;         // dữ liệu trả về
    private String path;    // endpoint
    private Instant timestamp; 

    public static <T> ApiResponse<T> ok(T data, String message, String path) {
        return ApiResponse.<T>builder()
                .status("success")
                .code(200)
                .message(message)
                .data(data)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int code, String message, String path) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(code)
                .message(message)
                .data(null)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }

    //Hiển thị chi tiet lỗi validate
    public static <T> ApiResponse<T> error(int code, String message, String path, T errors) {
        return ApiResponse.<T>builder()
                .status("error")
                .code(code)
                .message(message)
                .data(errors)
                .path(path)
                .timestamp(Instant.now())
                .build();
    }
}
