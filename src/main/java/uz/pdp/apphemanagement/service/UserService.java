package uz.pdp.apphemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.repository.UserRepository;

import java.util.List;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> get() {
        return userRepository.findAll();
    }
}
