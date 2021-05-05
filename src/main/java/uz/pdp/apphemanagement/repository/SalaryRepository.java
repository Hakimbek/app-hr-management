package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.apphemanagement.entity.Salary;

import java.util.List;
import java.util.UUID;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findAllByUserId(UUID user_id);
}
