package uz.pdp.apphemanagement.payload;

import lombok.Data;

import java.util.UUID;

@Data
public class SalaryDto {
    private Double salary;
    private UUID userId;
}
