package com.juls.sowlstudios.dto.response;


import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String fullName;
}