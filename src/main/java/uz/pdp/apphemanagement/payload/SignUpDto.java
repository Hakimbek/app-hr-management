package uz.pdp.apphemanagement.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SignUpDto {
    @NotNull
    @Size(min = 3, max = 50)
    private String firstName; // user firstName

    @NotNull
    @Size(min = 3, max = 50)
    private String lastName; // user lastName

    @NotNull
    @Email
    private String email; // user email

    @NotNull
    @Size(min = 8)
    private String password; // user password

    private Integer roleId; // user role
}
