package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "he_thong")
@Data
public class HeThong {
    @Id
    @Column(length = 36)
    private String id;
    private String name;
}