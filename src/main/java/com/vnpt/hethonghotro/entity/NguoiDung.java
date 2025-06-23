package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data; // Hoặc tự tạo Getters/Setters

@Data
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @Column(name = "username", length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 128)
    private String password;

    @Column(name = "employee_id", nullable = false, unique = true, length = 10)
    private String employee_id;

    @Column(name = "name", nullable = false, length = 254)
    private String name;

    @Column(name = "gender", nullable = false)
    private boolean gender;

    @Column(name = "phone", nullable = false, unique = true, length = 15)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Column(name = "address", nullable = false, columnDefinition = "TEXT")
    private String address;

    @Column(name = "avatar", nullable = false, columnDefinition = "TEXT")
    private String avatar;

    @Column(name = "is_deleted", nullable = false)
    private boolean is_deleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong_ban", nullable = false)
    private PhongBan phong_ban;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chuc_vu", nullable = false)
    private ChucVu chuc_vu;
}