package uz.pdp.apphemanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.pdp.apphemanagement.entity.Role;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.entity.enums.RoleName;
import uz.pdp.apphemanagement.repository.RoleRepository;
import uz.pdp.apphemanagement.repository.UserRepository;

import java.util.Collections;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserRepository userRepository;


    @Value(value = "${spring.datasource.initialization-mode}")
    private String initialMode;

    @Override
    public void run(String... args) throws Exception {
        if (initialMode.equals("always")) {

            // add DIRECTOR role
            Role director = new Role(RoleName.DIRECTOR);
            roleRepository.save(director);

            // add HR_MANAGER role
            Role hrManager = new Role(RoleName.HR_MANAGER);
            roleRepository.save(hrManager);

            // add EMPLOYEE role
            Role employee = new Role(RoleName.EMPLOYEE);
            roleRepository.save(employee);

            // add DIRECTOR user
            User user = new User("Bahramov",
                    "Hakimbek",
                    "abduhakim.bahramov@gmail.com",
                    passwordEncoder.encode("123456789"),
                    Collections.singleton(roleRepository.findByRoleName(RoleName.DIRECTOR)));
            user.setEnabled(true);
            userRepository.save(user);
        }
    }
}
