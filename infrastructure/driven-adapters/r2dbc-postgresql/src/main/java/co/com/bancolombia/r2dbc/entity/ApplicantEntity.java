package co.com.bancolombia.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("applicants")
public record ApplicantEntity(
        @Id String id,
        String names,
        @Column("last_names")
        String lastNames,
        @Column("document_number")
        String documentNumber,
        @Column("birth_date")
        LocalDate birthdate,
        String address,
        String phone,
        String email,
        @Column("base_salary")
        BigDecimal baseSalary
) {
}