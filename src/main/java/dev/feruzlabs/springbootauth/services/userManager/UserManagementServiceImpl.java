package dev.feruzlabs.springbootauth.services.userManager;

import dev.feruzlabs.springbootauth.dto.response.UserResponse;
import dev.feruzlabs.springbootauth.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private UserRepository userRepository;

    @Autowired
    public UserManagementServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable, String search) {
        return userRepository.findAll(pageable).map(UserResponse::fromEntity);
    }
}
