package com.user_service.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public class RegisterUserDto {
    private String email;

    private String password;

    private String fullName;
    private boolean emailVerified;

    private List<String> roles = new ArrayList<>();
}
