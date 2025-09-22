package dev.feruzlabs.springbootauth.services;

import dev.feruzlabs.springbootauth.dto.response.JwtResponse;
import dev.feruzlabs.springbootauth.dto.response.MessageResponse;
import dev.feruzlabs.springbootauth.entities.RefreshToken;
import dev.feruzlabs.springbootauth.entities.User;
import dev.feruzlabs.springbootauth.repositories.RefreshTokenRepository;
import dev.feruzlabs.springbootauth.repositories.UserRepository;
import dev.feruzlabs.springbootauth.securities.EnhancedJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EnhancedJwtUtil jwtUtil;

    /**
     * User uchun yangi refresh token yaratish
     */
    public RefreshToken createRefreshToken(User user) {
        // Eski tokenlarni o'chirish (ixtiyoriy - bir user uchun bitta token)
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); // Random token
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(7)); // 7 kun

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Refresh token orqali access token yangilash
     */
    public JwtResponse refreshToken(String refreshTokenStr) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(refreshTokenStr);

        if (refreshTokenOpt.isEmpty()) {
            throw new RuntimeException("Refresh token topilmadi!");
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // Token expire bo'lganligini tekshirish
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token muddati tugagan!");
        }

        // Token revoke qilinganligini tekshirish
        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token bekor qilingan!");
        }

        User user = refreshToken.getUser();

        // Yangi access token yaratish
        String newAccessToken = jwtUtil.generateToken(user);

        // Ixtiyoriy: Yangi refresh token ham yaratish (rotation)
        RefreshToken newRefreshToken = createRefreshToken(user);

        return new JwtResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * Refresh token ni tekshirish
     */
    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        if (refreshTokenOpt.isEmpty()) {
            return false;
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // Expire va revoke tekshirish
        return !refreshToken.getExpiryDate().isBefore(LocalDateTime.now()) &&
                !refreshToken.isRevoked();
    }

    /**
     * User ning barcha refresh tokenlarini bekor qilish
     */
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    /**
     * User ning barcha refresh tokenlarini o'chirish
     */
    public void deleteAllUserTokens(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    /**
     * Aniq bir tokenni bekor qilish
     */
    public MessageResponse revokeToken(String token) {
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        if (refreshTokenOpt.isEmpty()) {
            return new MessageResponse("Token topilmadi", false);
        }

        RefreshToken refreshToken = refreshTokenOpt.get();
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        return new MessageResponse("Token muvaffaqiyatli bekor qilindi", true);
    }

    /**
     * Muddati tugagan tokenlarni tozalash (scheduled task)
     * Har kuni soat 02:00 da ishga tushadi
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        refreshTokenRepository.deleteExpiredTokens(now);
        System.out.println("Expired refresh tokens cleaned up at: " + now);
    }

    /**
     * User statistikasi - nechta aktiv refresh token
     */
    public long getActiveTokenCountForUser(User user) {
        return refreshTokenRepository.findAll().stream()
                .filter(token -> token.getUser().equals(user))
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now()))
                .count();
    }

    /**
     * Barcha aktiv tokenlar soni
     */
    public long getTotalActiveTokens() {
        return refreshTokenRepository.findAll().stream()
                .filter(token -> !token.isRevoked())
                .filter(token -> token.getExpiryDate().isAfter(LocalDateTime.now()))
                .count();
    }
}
