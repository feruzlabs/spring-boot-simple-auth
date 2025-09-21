package dev.feruzlabs.springbootauth.services;

import dev.feruzlabs.springbootauth.dto.request.LoginRequest;
import dev.feruzlabs.springbootauth.dto.request.RegisterRequest;
import dev.feruzlabs.springbootauth.dto.response.JwtResponse;
import dev.feruzlabs.springbootauth.dto.response.MessageResponse;
import dev.feruzlabs.springbootauth.entities.User;
import dev.feruzlabs.springbootauth.repositories.UserRepository;
import dev.feruzlabs.springbootauth.securities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * User registratsiya qilish
     */
    public MessageResponse register(RegisterRequest request) {
        // Username band emasligini tekshirish
        if (userRepository.existsByUsername(request.getUsername())) {
            return new MessageResponse("Username allaqachon mavjud!");
        }

        // Email band emasligini tekshirish (ixtiyoriy)
        if (userRepository.existsByEmail(request.getEmail())) {
            return new MessageResponse("Email allaqachon ro'yxatdan o'tgan!");
        }

        // Yangi user yaratish
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        // Role default qiymati USER bo'ladi (Entity da belgilangan)

        try {
            userRepository.save(user);
            return new MessageResponse("User muvaffaqiyatli ro'yxatdan o'tdi!");
        } catch (Exception e) {
            return new MessageResponse("Ro'yxatdan o'tishda xatolik yuz berdi: " + e.getMessage());
        }
    }

    /**
     * User login qilish
     */
    public Object login(LoginRequest request) {
        // Username mavjudligini tekshirish
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isEmpty()) {
            return new MessageResponse("Username yoki parol xato!");
        }

        User user = userOpt.get();

        // Parolni tekshirish
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return new MessageResponse("Username yoki parol xato!");
        }

        // Token yaratish va qaytarish
        String token = jwtUtil.generateToken(user.getUsername());
        return new JwtResponse(token, user.getUsername(), user.getRole().name());
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

}
