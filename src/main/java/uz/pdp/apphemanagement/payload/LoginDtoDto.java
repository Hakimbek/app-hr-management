package uz.pdp.apphemanagement.payload;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class LoginDtoDto {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;
}
