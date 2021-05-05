package uz.pdp.apphemanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import uz.pdp.apphemanagement.entity.Salary;
import uz.pdp.apphemanagement.entity.User;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.SalaryDto;
import uz.pdp.apphemanagement.repository.SalaryRepository;
import uz.pdp.apphemanagement.repository.UserRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SalaryService {
    @Autowired
    SalaryRepository salaryRepository;
    @Autowired
    UserRepository userRepository;


    /**
     * ADD SALARY
     *
     * @param salaryDto Salary,
     *                  UserId
     * @return ApiResponse in ResponseEntity
     */
    public ApiResponse add(SalaryDto salaryDto) {
        Salary salary = new Salary();
        salary.setSalary(salaryDto.getSalary());

        Optional<User> optionalUser = userRepository.findById(salaryDto.getUserId());
        if (!optionalUser.isPresent()) {
            return new ApiResponse("User not found", false);
        }
        User user = optionalUser.get();
        salary.setUser(user);
        salaryRepository.save(salary);
        return new ApiResponse("Successfully added", true);
    }


    /**
     * GET SALARIES OF USER
     *
     * @param id UUID
     * @return ApiResponse in ResponseEntity
     */
    public List<Salary> getById(UUID id) {
        return salaryRepository.findAllByUserId(id);
    }
}
