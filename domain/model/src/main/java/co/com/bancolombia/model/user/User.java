package co.com.bancolombia.model.user;

import co.com.bancolombia.valueobject.Email;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
  private String id; // UUID
  private String applicantId; // link applicant
  private Email email; // login
  private String passwordHash;
  private Set<Role> roles;
  private boolean enabled;
}
