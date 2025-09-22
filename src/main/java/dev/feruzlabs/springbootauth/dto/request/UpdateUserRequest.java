package dev.feruzlabs.springbootauth.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled = true;
}
