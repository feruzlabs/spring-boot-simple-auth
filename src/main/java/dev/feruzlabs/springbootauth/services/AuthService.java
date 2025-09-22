package dev.feruzlabs.springbootauth.services;

import dev.feruzlabs.springbootauth.dto.request.LoginRequest;
import dev.feruzlabs.springbootauth.dto.request.RegisterRequest;
import dev.feruzlabs.springbootauth.dto.response.JwtResponse;
import dev.feruzlabs.springbootauth.dto.response.MessageResponse;
import dev.feruzlabs.springbootauth.entities.RefreshToken;
import dev.feruzlabs.springbootauth.entities.User;
import dev.feruzlabs.springbootauth.repositories.UserRepository;
import dev.feruzlabs.springbootauth.securities.EnhancedJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EnhancedJwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    /**
     * User registratsiya qilish
     */
    public MessageResponse register(RegisterRequest request) {
        // Username band emasligini tekshirish
        if (userRepository.existsByUsername(request.getUsername())) {
            return new MessageResponse("Username allaqachon mavjud!");
        }

        // Email band emasligini tekshirish
        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse("Email allaqachon ro'yxatdan o'tgan!");
        }

        // Yangi user yaratish
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        try {
            userRepository.save(user);
            return new MessageResponse("User muvaffaqiyatli ro'yxatdan o'tdi!", true);
        } catch (Exception e) {
            return new MessageResponse("Ro'yxatdan o'tishda xatolik yuz berdi: " + e.getMessage());
        }
    }

    /**
     * User login qilish
     */
    public Object login(LoginRequest request, String clientIp) {
        // Username mavjudligini tekshirish
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return new MessageResponse("Username yoki parol xato!");
        }

        User user = userOpt.get();

        // User enabled ekanligini tekshirish
        if (!user.isEnabled()) {
            return new MessageResponse("Account bloklangan. Admin bilan bog'laning.");
        }

        // Parolni tekshirish
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new MessageResponse("Username yoki parol xato!");
        }

        // Last login vaqtini yangilash
        user.setLastLoginAt(LocalDateTime.now());
        user.setLastLoginIp(clientIp);
        userRepository.save(user);

        // JWT va Refresh token yaratish
        String accessToken = jwtUtil.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new JwtResponse(
                accessToken,
                refreshToken.getToken(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * User logout - refresh tokenni bekor qilish
     */
    public MessageResponse logout(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            refreshTokenService.deleteAllUserTokens(userOpt.get());
            return new MessageResponse("Successfully logged out", true);
        }

        return new MessageResponse("User not found", false);
    }

    /**
     * Barcha devicelardan logout
     */
    public MessageResponse logoutFromAllDevices(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            refreshTokenService.revokeAllUserTokens(userOpt.get());
            return new MessageResponse("Successfully logged out from all devices", true);
        }

        return new MessageResponse("User not found", false);
    }

    /**
     * Joriy user ma'lumotlarini olish
     */
    public Object getCurrentUserInfo(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new Object() {
                public Long id = user.getId();
                public String username = user.getUsername();
                public String email = user.getEmail();
                public String role = user.getRole().name();
                public boolean enabled = user.isEnabled();
                public LocalDateTime createdAt = user.getCreatedAt();
                public LocalDateTime lastLoginAt = user.getLastLoginAt();
                public long activeTokens = refreshTokenService.getActiveTokenCountForUser(user);
            };
        }

        return new MessageResponse("User not found", false);
    }

    /**
     * Parolni o'zgartirish
     */
    public MessageResponse changePassword(String username, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return new MessageResponse("User not found", false);
        }

        User user = userOpt.get();

        // Joriy parolni tekshirish
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return new MessageResponse("Joriy parol noto'g'ri", false);
        }

        // Yangi parolni o'rnatish
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xavfsizlik uchun barcha tokenlarni bekor qilish
        refreshTokenService.revokeAllUserTokens(user);

        return new MessageResponse("Parol muvaffaqiyatli o'zgartirildi", true);
    }

    /**
     * Username mavjudligini tekshirish (utility method)
     */
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * User topish username orqali
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Password reset qilish
     */
    public MessageResponse resetPassword(String token, String newPassword) {
        // Bu yerda reset token logic bo'lishi kerak
        // Hozircha placeholder
        return new MessageResponse("Password reset functionality coming soon", false);
    }


    /**
     * Password reset jarayonini boshlash
     */
    public MessageResponse initPasswordReset(String email) {
        // Production da email yuborish kerak
        // Hozircha faqat message qaytaramiz
        return new MessageResponse("Agar email mavjud bo'lsa, reset link yuborildi", true);
    }
}
