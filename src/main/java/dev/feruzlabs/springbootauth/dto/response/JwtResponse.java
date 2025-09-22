package dev.feruzlabs.springbootauth.dto.response;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String type = "Bearer";
    private String username;
    private String role;
    private long expiresIn = 86400; // 24 hours in seconds

    // Constructors
    public JwtResponse(String token, String username, String role) {
        this.accessToken = token;
        this.username = username;
        this.role = role;
    }
    public JwtResponse(String accessToken, String refreshToken, String username, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.username = username;
        this.role = role;
    }

}
