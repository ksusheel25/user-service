package com.user_service.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDto {

    @NotNull(message = "email is required to login")
    @Email
    private String email;

    @NotNull(message = "password is required")
    private String password;
}
