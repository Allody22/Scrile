package ru.scrile.org.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    @NotBlank(message = "Name must not be blank")
    private String username;

    @NotBlank(message = "Password must not be blank")
    private String password;
}
