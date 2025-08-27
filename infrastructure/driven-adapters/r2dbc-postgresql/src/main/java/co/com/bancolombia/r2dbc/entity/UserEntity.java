package co.com.bancolombia.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record UserEntity(
        @Id
        String id,
        @Column("applicant_id")
        String applicantId,
        String email,
        @Column("password_hash")
        String passwordHash,
        @Column("roles_csv")
        String rolesCsv,
        boolean enabled
) {
}
