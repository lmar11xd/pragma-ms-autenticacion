package co.com.bancolombia.model.loginattempt;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoginAttempt {
    private String email;
    private int attempts;
    private LocalDateTime lastAttempt;
    private LocalDateTime lockedUntil;

    public boolean isLocked() {
        return lockedUntil != null && lockedUntil.isAfter(LocalDateTime.now());
    }

    public void increment() {
        this.attempts++;
    }

    public void reset() {
        this.attempts = 0;
        this.lockedUntil = null;
    }
}
