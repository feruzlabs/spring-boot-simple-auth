package dev.feruzlabs.springbootauth.controllers;

import dev.feruzlabs.springbootauth.dto.response.UserResponse;
import dev.feruzlabs.springbootauth.payload.ApiResponse;
import dev.feruzlabs.springbootauth.services.userManager.UserManagementService;
import dev.feruzlabs.springbootauth.utils.SortUtils;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserController {
    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(0) @Max(100) int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            @RequestParam(required = false) String filter
    ) {
        Sort sort = SortUtils.buildSort(sortBy, order);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponse> users = userManagementService.getAllUsers(pageable, filter);

        return ResponseEntity.ok(ApiResponse.success(users, "Userlar muvofaqqiyatli olindi!"));
    }
}
