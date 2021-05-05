package uz.pdp.apphemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.LoginDtoDto;
import uz.pdp.apphemanagement.payload.RegisterDto;
import uz.pdp.apphemanagement.payload.UserEditorDto;
import uz.pdp.apphemanagement.service.AuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    /**
     * REGISTER USERS IN THE SYSTEM
     *
     * @param registerDto firstName(String),
     *                    lastName(String),
     *                    email(String),
     *                    password(String),
     *                    roleId(Integer)
     * @return ApiResponse in ResponseEntity
     */
    @PostMapping("/register")
    public ResponseEntity<?> signUp(@RequestBody RegisterDto registerDto) {
        ApiResponse apiResponse = authService.register(registerDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 201 : 409).body(apiResponse);
    }


    /**
     * LOGIN TO SYSTEM
     *
     * @param loginDtoDto email(String),
     *                    password(String)
     * @return ApiResponse in ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<?> signIn(@RequestBody LoginDtoDto loginDtoDto) {
        ApiResponse apiResponse = authService.login(loginDtoDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


    /**
     * VERIFY USER ACCOUNT
     *
     * @param emailCode   String
     * @param email       String
     * @param loginDtoDto email(String),
     *                    password(String)
     * @return ApiResponse in ResponseEntity
     */
    @PostMapping("/verifyAccount")
    public ResponseEntity<?> verify(@RequestParam String emailCode, @RequestParam String email, @RequestBody LoginDtoDto loginDtoDto) {
        ApiResponse apiResponse = authService.verifyAccount(email, emailCode, loginDtoDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


    /**
     * LOGOUT FROM SYSTEM
     *
     * @return ApiResponse in ResponseEntity
     */
    @GetMapping("/logout")
    public ResponseEntity<?> logout() {
        ApiResponse apiResponse = authService.logout();
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }


    /**
     * EDIT USER
     *
     * @param userEditorDto firstName(String),
     *                    lastName(String),
     *                    password(String),
     * @return ApiResponse in ResponseEntity
     */
    @PutMapping("/edit")
    public ResponseEntity<?> edit(@RequestBody UserEditorDto userEditorDto) {
        ApiResponse apiResponse = authService.edit(userEditorDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 200 : 409).body(apiResponse);
    }
}
