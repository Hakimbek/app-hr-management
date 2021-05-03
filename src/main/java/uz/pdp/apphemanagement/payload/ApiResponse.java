package uz.pdp.apphemanagement.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {

    private String message;

    private boolean success;

    private Object object;

    // constructor
    public ApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }
}
