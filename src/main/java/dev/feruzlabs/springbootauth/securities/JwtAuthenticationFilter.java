package dev.feruzlabs.springbootauth.securities;

import dev.feruzlabs.springbootauth.entities.User;
import dev.feruzlabs.springbootauth.enums.Role;
import dev.feruzlabs.springbootauth.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    // Skip qilinadigan yo'llar
    private final List<String> skipPaths = List.of(
            "/api/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-resources",
            "/webjars",
            "/configuration",
            "/favicon.ico"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldSkip = skipPaths.stream().anyMatch(path::startsWith);
        logger.info("Path: " + path + ", Should skip filter: " + shouldSkip);
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        logger.info("JWT Filter ishlamoqda: " + request.getRequestURI());

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Authorization header dan token olish
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            logger.info("JWT token topildi: " + jwt.substring(0, Math.min(20, jwt.length())) + "...");
            try {
                username = jwtUtil.getUsernameFromToken(jwt);
                logger.info("Username extracted: " + username);
            } catch (Exception e) {
                logger.error("JWT token dan username olib bo'lmadi: " + e.getMessage(), e);
            }
        } else {
            logger.info("Authorization header yo'q yoki noto'g'ri format");
        }

        // Token to'g'ri bo'lsa, Spring Security context ga qo'shish
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("Username mavjud, user ni qidirayapmiz: " + username);

            Optional<User> userOpt = userRepository.findByUsername(username);

            if (userOpt.isPresent()) {
                logger.info("User topildi: " + userOpt.get().getUsername());
                if (jwtUtil.validateToken(jwt, username)) {
                    logger.info("Token valid, authentication o'rnatilayapti");
                    User user = userOpt.get();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user.getUsername(),
                                    null,
                                    getAuthorities(user.getRole())
                            );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.info("Authentication muvaffaqiyatli o'rnatildi");
                } else {
                    logger.error("Token invalid");
                }
            } else {
                logger.error("User topilmadi: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
