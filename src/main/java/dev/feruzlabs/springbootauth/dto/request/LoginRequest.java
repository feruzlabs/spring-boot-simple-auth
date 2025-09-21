package dev.feruzlabs.springbootauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username bo'sh bo'lishi mumkin emas")
    private String username;

    @NotBlank(message = "Parol bo'sh bo'lishi mumkin emas")
    private String password;
}
