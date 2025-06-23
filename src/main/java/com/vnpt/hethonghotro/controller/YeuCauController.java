package com.vnpt.hethonghotro.controller;

import com.vnpt.hethonghotro.dto.ThaoTacRequest;
import com.vnpt.hethonghotro.service.YeuCauService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/yeu-cau")
@RequiredArgsConstructor
public class YeuCauController {
    private final YeuCauService yeuCauService;

    @PostMapping("/{id}/thao-tac")
    public ResponseEntity<?> xuLyHanhDong(
            @PathVariable String id,
            @Valid @RequestBody ThaoTacRequest request,
            Authentication authentication
    ) {
        try {
            String username = authentication.getName();
            yeuCauService.xuLyHanhDong(id, request, username);

            String message = "Thao tác '" + request.getThao_tac() + "' đã được thực hiện thành công.";
            return ResponseEntity.ok(Map.of("message", message));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}