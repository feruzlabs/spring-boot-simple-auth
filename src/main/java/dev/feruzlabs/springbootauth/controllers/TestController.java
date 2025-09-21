package dev.feruzlabs.springbootauth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test Controller", description = "Test API endpoints")
public class TestController {

    @GetMapping("/hello")
    @Operation(summary = "Protected Hello", description = "Authentication required")
    public ResponseEntity<String> hello(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok("Salom, " + username + "! Bu himoyalangan API.");
    }

    @GetMapping("/profile")
    @Operation(summary = "User Profile", description = "Get current user profile")
    public ResponseEntity<Object> getUserProfile(Authentication authentication) {
        return ResponseEntity.ok(new Object() {
            public String username = authentication.getName();
            public String authorities = authentication.getAuthorities().toString();
            public boolean authenticated = authentication.isAuthenticated();
            public String message = "Bu sizning profilingiz";
        });
    }

    @GetMapping("/admin/page")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin Only", description = "Only for ADMIN role")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Bu faqat admin uchun!");
    }

    @GetMapping("/user/page")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "User Only", description = "Only for USER role")
    public ResponseEntity<String> userOnly() {
        return ResponseEntity.ok("Bu faqat user uchun!");
    }

    @PostMapping("/echo")
//    @Operation(summary = "Echo message", description = "Returns the same message")
    public ResponseEntity<String> echo(@RequestBody String message) {
        return ResponseEntity.ok("Echo: " + message);
    }


    // JWT token bilan ishlaydigan endpoint
    @GetMapping("/secure")
    @Operation(summary = "Secure endpoint", description = "Authentication required")
    public ResponseEntity<String> secureEndpoint(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("Authentication is null!");
        }
        return ResponseEntity.ok("Hello " + authentication.getName() + "! This is secure endpoint.");
    }

    // Debug uchun
    @GetMapping("/debug")
    @Operation(summary = "Debug endpoint", description = "Shows authentication info")
    public ResponseEntity<Object> debug(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("No authentication found");
        }

        return ResponseEntity.ok(new Object() {
            public String name = authentication.getName();
            public String authorities = authentication.getAuthorities().toString();
            public boolean authenticated = authentication.isAuthenticated();
        });
    }
}