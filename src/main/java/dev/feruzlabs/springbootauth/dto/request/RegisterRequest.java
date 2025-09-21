package dev.feruzlabs.springbootauth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Username bo'sh bo'lishi mumkin emas")
    @Size(min = 3, max = 20, message = "Username 3-20 ta belgi orasida bo'lishi kerak")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lishi mumkin emas")
    @Size(min = 6, max = 40, message = "Parol kamida 6 ta belgi bo'lishi kerak")
    private String password;

    @NotBlank(message = "Email bo'sh bo'lishi mumkin emas")
    @Email(message = "Email formati noto'g'ri")
    @Size(max = 50, message = "Email juda uzun")
    private String email;

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
