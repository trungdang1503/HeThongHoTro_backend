package com.vnpt.hethonghotro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // Tạo constructor với tất cả các tham số
public class AuthResponse {
    private String token;
}