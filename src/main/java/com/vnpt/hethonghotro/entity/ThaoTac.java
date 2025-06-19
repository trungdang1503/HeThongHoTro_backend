package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "thao_tac")
@Data
public class ThaoTac {
    @Id
    @Column(length = 36)
    private String id;
    private String name;
}