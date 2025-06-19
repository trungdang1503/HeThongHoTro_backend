package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "phong_ban")
@Data
public class PhongBan {
    @Id
    @Column(length = 36)
    private String id;
    private String name;
    private String phone;
    @Column(name = "is_deleted")
    private boolean is_deleted;
}