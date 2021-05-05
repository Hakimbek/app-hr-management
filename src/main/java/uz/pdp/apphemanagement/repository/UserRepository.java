package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.apphemanagement.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(String email); // check out, this email exist or not

    Optional<User> findByEmail(String email); // find user by email

    Optional<User> findByEmailAndEmailCode(String email, String emailCode);
}
