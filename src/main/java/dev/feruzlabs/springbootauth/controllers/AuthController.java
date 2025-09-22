package dev.feruzlabs.springbootauth.controllers;

import dev.feruzlabs.springbootauth.dto.CurrentUserDTO;
import dev.feruzlabs.springbootauth.dto.request.*;
import dev.feruzlabs.springbootauth.dto.response.JwtResponse;
import dev.feruzlabs.springbootauth.dto.response.MessageResponse;
import dev.feruzlabs.springbootauth.services.AuthService;
import dev.feruzlabs.springbootauth.services.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * User registratsiya qilish
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {

        MessageResponse response = authService.register(registerRequest);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * User login qilish
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request) {
        Object response = authService.login(loginRequest, getClientIp(request));

        if (response instanceof MessageResponse) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse response = refreshTokenService.refreshToken(request.getRefreshToken());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Refresh token invalid: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        authService.logout(authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Successfully logged out"));
    }

    @PostMapping("/logout/all")
    public ResponseEntity<?> logoutFromAllDevices(Authentication authentication) {
        authService.logoutFromAllDevices(authentication.getName());
        return ResponseEntity.ok(new MessageResponse("Successfully logged out from all devices"));
    }

    /**
     * Username mavjudligini tekshirish (ixtiyoriy)
     * GET /api/auth/check-username?username=test
     */
    @GetMapping("/username/check")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {

        boolean exists = authService.isUsernameExists(username);

        if (exists) {
            return ResponseEntity.ok(new MessageResponse("Username allaqachon mavjud", false));
        } else {
            return ResponseEntity.ok(new MessageResponse("Username mavjud", true));
        }
    }

    // Password reset functionality
    @PostMapping("/password/forget")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        MessageResponse response = authService.initPasswordReset(request.getEmail());
        return ResponseEntity.ok(response); // Always return 200 for security
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        MessageResponse response = authService.resetPassword(
                request.getToken(),
                request.getNewPassword()
        );

        return response.isSuccess() ?
                ResponseEntity.ok(response) :
                ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request,
                                            Authentication authentication) {
        MessageResponse response = authService.changePassword(
                authentication.getName(),
                request.getCurrentPassword(),
                request.getNewPassword()
        );

        return response.isSuccess() ?
                ResponseEntity.ok(response) :
                ResponseEntity.badRequest().body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        CurrentUserDTO userDTO = (CurrentUserDTO) authentication.getPrincipal();
        return ResponseEntity.ok(authService.getCurrentUserInfo(userDTO.getUsername()));
    }
}
