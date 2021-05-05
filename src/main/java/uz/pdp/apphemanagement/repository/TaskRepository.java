package uz.pdp.apphemanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.apphemanagement.entity.Task;
import uz.pdp.apphemanagement.entity.enums.TaskStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    Optional<Task> findByTaskCode(String taskCode);

    Optional<Task> findByTaskStatusAndUserId(TaskStatus taskStatus, UUID user_id);

    List<Task> findAllByUserId(UUID user_id);

    Optional<Task> findByCreatedAtBetweenAndUserId(Timestamp createdAt, Timestamp createdAt2, UUID user_id);
}
