package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "chuc_vu")
@Data
public class ChucVu {
    @Id
    @Column(length = 36)
    private String id;
    private String name;
    @Column(name = "is_deleted")
    private boolean is_deleted;
}