package uz.pdp.apphemanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.pdp.apphemanagement.entity.Salary;
import uz.pdp.apphemanagement.payload.ApiResponse;
import uz.pdp.apphemanagement.payload.SalaryDto;
import uz.pdp.apphemanagement.service.SalaryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/salary")
public class SalaryController {
    @Autowired
    SalaryService salaryService;

    /**
     * ADD SALARY
     *
     * @param salaryDto Salary,
     *                  UserId
     * @return ApiResponse in ResponseEntity
     */
    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody SalaryDto salaryDto) {
        ApiResponse apiResponse = salaryService.add(salaryDto);
        return ResponseEntity.status(apiResponse.isSuccess() ? 201 : 409).body(apiResponse);
    }


    /**
     * GET SALARIES OF USER
     *
     * @param id UUID
     * @return ApiResponse in ResponseEntity
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        List<Salary> salaries = salaryService.getById(id);
        return ResponseEntity.status(salaries != null ? 201 : 409).body(salaries);
    }
}
