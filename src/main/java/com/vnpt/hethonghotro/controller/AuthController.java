package com.vnpt.hethonghotro.controller;

import com.vnpt.hethonghotro.dto.AuthRequest;
import com.vnpt.hethonghotro.dto.AuthResponse;
import com.vnpt.hethonghotro.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            // 1. Xác thực người dùng bằng username và password
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Sai tên đăng nhập hoặc mật khẩu", e);
        }

        // 2. Nếu xác thực thành công, tải thông tin UserDetails (bao gồm cả vai trò)
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(authRequest.getUsername());

        // 3. Tạo JWT token từ UserDetails
        final String jwt = jwtUtil.generateToken(userDetails);

        // 4. Trả về token cho client
        return ResponseEntity.ok(new AuthResponse(jwt));
    }
}
