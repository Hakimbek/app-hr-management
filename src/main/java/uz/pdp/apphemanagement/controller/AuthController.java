package uz.pdp.apphemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.SignInDto;
import uz.pdp.apphemanagement.payload.SignUpDto;
import uz.pdp.apphemanagement.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    /**
     * SIGN UP USER
     *
     * @param signUpDto firstName(String), lastName(String), email(String), password(String), roles
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody SignUpDto signUpDto) {
        ApiResponse apiResponse = authService.signUp(signUpDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 201 : 409).body(apiResponse);
    }


    /**
     * SIGN IN TO SYSTEM
     *
     * @param signInDto email, password
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInDto) {
        ApiResponse apiResponse = authService.signIn(signInDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


    /**
     * VERIFY USER ACCOUNT
     *
     * @param emailCode String
     * @param email String
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    @PostMapping("/verifyAccount")
    public ResponseEntity<?> verify(@RequestParam String emailCode, @RequestParam String email, @RequestBody SignInDto signInDto) {
        ApiResponse apiResponse = authService.verifyAccount(email, emailCode, signInDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }
}
