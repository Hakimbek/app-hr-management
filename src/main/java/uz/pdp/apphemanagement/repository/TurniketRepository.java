package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.apphemanagement.entity.Turniket;

import java.util.Optional;
import java.util.UUID;

public interface TurniketRepository extends JpaRepository<Turniket, Integer> {
    Optional<Turniket> findByUserId(UUID user_id);
}
