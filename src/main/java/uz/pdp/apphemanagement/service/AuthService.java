package uz.pdp.apphemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
import uz.pdp.apphemanagement.entity.Role;
import uz.pdp.apphemanagement.entity.Turniket;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.entity.enums.RoleName;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.LoginDtoDto;
import uz.pdp.apphemanagement.payload.RegisterDto;
import uz.pdp.apphemanagement.payload.UserEditorDto;
import uz.pdp.apphemanagement.repository.RoleRepository;
import uz.pdp.apphemanagement.repository.TurniketRepository;
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
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    TurniketRepository turniketRepository;
    @Autowired
    JavaMailSender javaMailSender;


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
    public ApiResponse register(RegisterDto registerDto) {

        // tizimda shunday email yoqligini tekshiradi
        boolean existsByEmail = userRepository.existsByEmail(registerDto.getEmail());
        if (existsByEmail) {
            return new ApiResponse("This Email already exist", false);
        }

        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setSalary(registerDto.getSalary());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmailCode(UUID.randomUUID().toString());

        // shunday role bor yoqligini tekshiradi
        Optional<Role> optionalRole = roleRepository.findById(registerDto.getRoleId());
        if (!optionalRole.isPresent()) {
            return new ApiResponse("Role not found", false);
        }
        Role signUpRole = optionalRole.get();

        // role direktor emasligini tekshiradi (faqat bitta direktor bo'ladi)
        if (signUpRole.getRoleName().equals(RoleName.DIRECTOR)) {
            return new ApiResponse("Director already exist", false);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userInSystem = (User) authentication.getPrincipal();

        // qo'shilayotgan userning roli HR_MANAGER bo'lsa
        if (signUpRole.getRoleName().equals(RoleName.HR_MANAGER)) {
            boolean roleIsDirector = false;

            // qo'shayotgan user DIRECTOR ligini tekshiradi
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
        } else { // qo'shilayotgan userning roli EMPLOYEE bo'lsa
            boolean roleIsEmployee = false;

            // qo'shayotgan user EMPLOYEE emasligligini tekshiradi
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

        // send message to email
        sendEmail(savedUser.getEmail(), savedUser.getEmailCode());
        return new ApiResponse("Successfully registered, verify your account", true);
    }


    /**
     * LOGIN TO SYSTEM
     *
     * @param loginDtoDto email(String),
     *                    password(String)
     * @return ApiResponse in ResponseEntity
     */
    public ApiResponse login(LoginDtoDto loginDtoDto) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDtoDto.getEmail(), loginDtoDto.getPassword()));

            User user = (User) authenticate.getPrincipal();
            String token = jwtProvider.generateToken(user.getUsername(), user.getRoles());

            Turniket turniket = new Turniket();
            turniket.setUser(user);
            turniket.setActive(true);
            turniketRepository.save(turniket);

            return new ApiResponse("Token", true, token);
        } catch (BadCredentialsException e) {
            return new ApiResponse("Password or login is incorrect", false);
        }
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
    public ApiResponse verifyAccount(String email, String emailCode, LoginDtoDto loginDtoDto) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(true);
            user.setEmailCode(null);
            user.setPassword(passwordEncoder.encode(loginDtoDto.getPassword()));
            userRepository.save(user);

            Turniket turniket = new Turniket();
            turniket.setUser(user);
            turniket.setActive(true);
            turniketRepository.save(turniket);

            return new ApiResponse("Account is verified", true);
        }
        return new ApiResponse("Account already verified", false);
    }


    /**
     * LOG OUT FROM SYSTEM
     *
     * @return ApiResponse in ResponseEntity
     */
    public ApiResponse logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            User user = (User) authentication.getPrincipal();

            Optional<Turniket> optionalTurniket = turniketRepository.findByUserId(user.getId());
            if (!optionalTurniket.isPresent()) {
                return new ApiResponse("Tunuket not found", false);
            }
            Turniket turniket = optionalTurniket.get();
            turniket.setActive(false);
            turniketRepository.save(turniket);

            SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
            return new ApiResponse("Logged out", true);
        } catch (Exception e) {
            return new ApiResponse("Error", false);
        }
    }


    /**
     * EDIT USER
     *
     * @param userEditorDto firstName(String),
     *                      lastName(String),
     *                      password(String),
     * @return ApiResponse in ResponseEntity
     */
    public ApiResponse edit(UserEditorDto userEditorDto) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            User editedUser = optionalUser.get();
            editedUser.setFirstName(userEditorDto.getFirstName());
            editedUser.setLastName(userEditorDto.getLastName());
            editedUser.setPassword(passwordEncoder.encode(userEditorDto.getPassword()));
            userRepository.save(editedUser);
            return new ApiResponse("Successfully edited", true);
        } else {
            return new ApiResponse("Error", false);
        }
    }


    // send message to user email
    public void sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom("email.sender.hr@gmail.com");
            simpleMailMessage.setTo(sendingEmail);
            simpleMailMessage.setSubject("Verify account");
            simpleMailMessage.setText("http://localhost:8080/api/auth/verifyAccount?emailCode=" + emailCode + "&email=" + sendingEmail);
            javaMailSender.send(simpleMailMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // load user by username
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
