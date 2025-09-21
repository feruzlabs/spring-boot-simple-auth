package dev.feruzlabs.springbootauth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponse {
    private String message;
    private boolean success;

    // Constructors
    public MessageResponse(String message) {
        this.message = message;
        this.success = !message.contains("xato") && !message.contains("mavjud"); // Simple logic
    }

}
