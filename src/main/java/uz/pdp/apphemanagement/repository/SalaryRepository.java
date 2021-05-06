package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uz.pdp.apphemanagement.entity.Salary;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

public interface SalaryRepository extends JpaRepository<Salary, Integer> {
    List<Salary> findAllByUserId(UUID user_id);

    @Query(value = "select *\n" +
            "from salary s\n" +
            "where s.created_at > :timestamp1\n" +
            "  and s.created_at < :timestamp2", nativeQuery = true)
    public List<Salary> findAllSalary(Timestamp timestamp1, Timestamp timestamp2);
}
