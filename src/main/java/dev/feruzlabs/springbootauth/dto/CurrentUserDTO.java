package dev.feruzlabs.springbootauth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CurrentUserDTO {
    public Long id;
    public String username;
}
