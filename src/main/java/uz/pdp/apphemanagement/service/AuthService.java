package uz.pdp.apphemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.apphemanagement.config.JavaMailSenderConfig;
import uz.pdp.apphemanagement.entity.Role;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.entity.enums.RoleName;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.SignInDto;
import uz.pdp.apphemanagement.payload.SignUpDto;
import uz.pdp.apphemanagement.repository.RoleRepository;
import uz.pdp.apphemanagement.repository.UserRepository;
import uz.pdp.apphemanagement.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;


    /**
     * SIGN UP USER
     *
     * @param signUpDto firstName(String), lastName(String), email(String), password(String), roles
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    public ApiResponse signUp(SignUpDto signUpDto) {
        boolean existsByEmail = userRepository.existsByEmail(signUpDto.getEmail());
        if (existsByEmail) {
            return new ApiResponse("This Email already exist", false);
        }

        User user = new User();
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setEmailCode(UUID.randomUUID().toString());

        Optional<Role> optionalRole = roleRepository.findById(signUpDto.getRoleId());
        if (!optionalRole.isPresent()) {
            return new ApiResponse("Role not found", false);
        }
        Role signUpRole = optionalRole.get();

        if (signUpRole.getRoleName().equals(RoleName.DIRECTOR)) {
            return new ApiResponse("Director already exist", false);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userInSystem = (User) authentication.getPrincipal();

        if (signUpRole.getRoleName().equals(RoleName.HR_MANAGER)) {
            boolean roleIsDirector = false;

            for (Role role : userInSystem.getRoles()) {
                if (role.getRoleName().equals(RoleName.DIRECTOR)) {
                    user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.HR_MANAGER)));
                    roleIsDirector = true;
                    break;
                }
            }

            if (!roleIsDirector) {
                return new ApiResponse("Only DIRECTOR can add HR_MANAGER", false);
            }
        } else {
            boolean roleIsEmployee = false;

            for (Role role : userInSystem.getRoles()) {
                if (role.getRoleName().equals(RoleName.EMPLOYEE)) {
                    roleIsEmployee = true;
                    break;
                }
            }

            if (roleIsEmployee) {
                return new ApiResponse("EMPLOYEE can not add Users", false);
            } else {
                user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.EMPLOYEE)));
            }
        }
        User savedUser = userRepository.save(user);

        sendEmail(savedUser.getEmail(), savedUser.getEmailCode());
        return new ApiResponse("Successfully registered, verify your account", true);
    }


    // send message to user email
    public void sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("company.hr.manage@gmail.com");
            simpleMailMessage.setTo(sendingEmail);
            simpleMailMessage.setSubject("Verify account");
            simpleMailMessage.setText("<a href=" + "\"http://localhost:8080/api/verifyAccount?emailCode=" + emailCode + "&email=" + sendingEmail + "\">Verify</a>");
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * SIGN IN TO SYSTEM
     *
     * @param signInDto email, password
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    public ApiResponse signIn(SignInDto signInDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInDto.getEmail(), signInDto.getPassword()));

            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(user.getUsername(), user.getRoles());
            return new ApiResponse("Token", true, token);
        } catch (BadCredentialsException e) {
            return new ApiResponse("Password or login is incorrect", false);
        }
    }


    /**
     * VERIFY USER ACCOUNT
     *
     * @param emailCode String
     * @param email     String
     * @return API RESPONSE IN RESPONSE ENTITY
     */
    public ApiResponse verifyAccount(String email, String emailCode, SignInDto signInDto) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            user.setPassword(signInDto.getPassword());
            userRepository.save(user);
            return new ApiResponse("Account is verified", true);
        }
        return new ApiResponse("Account already verified", false);
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
