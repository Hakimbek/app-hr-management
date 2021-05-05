package uz.pdp.apphemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<?> get() {
        List<User> users = userService.get();
        return ResponseEntity.status(users != null ? 200 : 409).body(users);
    }

}
