package dev.feruzlabs.springbootauth.controllers;

import dev.feruzlabs.springbootauth.dto.request.LoginRequest;
import dev.feruzlabs.springbootauth.dto.request.RegisterRequest;
import dev.feruzlabs.springbootauth.dto.response.MessageResponse;
import dev.feruzlabs.springbootauth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {
    @Autowired
    private AuthService authService;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {

        Object response = authService.login(loginRequest);

        // MessageResponse bo'lsa - xatolik
        if (response instanceof MessageResponse) {
            return ResponseEntity.badRequest().body(response);
        }

        // JwtResponse bo'lsa - muvaffaqiyatli
        return ResponseEntity.ok(response);
    }

    /**
     * Username mavjudligini tekshirish (ixtiyoriy)
     * GET /api/auth/check-username?username=test
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {

        boolean exists = authService.isUsernameExists(username);

        if (exists) {
            return ResponseEntity.ok(new MessageResponse("Username allaqachon mavjud", false));
        } else {
            return ResponseEntity.ok(new MessageResponse("Username mavjud", true));
        }
    }
}
