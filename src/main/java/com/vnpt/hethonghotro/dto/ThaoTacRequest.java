package com.vnpt.hethonghotro.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ThaoTacRequest {
    @NotBlank(message = "Hành động không được để trống")
    private String thao_tac;
    private String noi_dung_phan_hoi;
}