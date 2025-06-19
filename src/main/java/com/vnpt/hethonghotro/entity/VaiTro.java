package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "vai_tro")
@Data
public class VaiTro {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 50)
    private String name;
}