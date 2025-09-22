package dev.feruzlabs.springbootauth.services.userManager;

import dev.feruzlabs.springbootauth.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserManagementService {
    public Page<UserResponse> getAllUsers(Pageable pageable, String search);
}
