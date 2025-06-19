package com.vnpt.hethonghotro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThaoTacRequest {
    // Không cần username vì chúng ta sẽ lấy từ JWT token của người đang đăng nhập
    @NotBlank(message = "Hành động không được để trống")
    private String thao_tac; // "tiep_nhan", "bo_qua", "phan_hoi", "ket_thuc"
    private String noi_dung_phan_hoi;
}