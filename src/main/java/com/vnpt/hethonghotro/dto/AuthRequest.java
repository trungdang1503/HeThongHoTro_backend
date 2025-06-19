package com.vnpt.hethonghotro.dto;

import lombok.Data;

@Data // Tự tạo getters/setters/constructors
public class AuthRequest {
    private String username;
    private String password;
}