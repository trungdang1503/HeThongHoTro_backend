package com.vnpt.hethonghotro.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_hoi")
@Data
public class PhanHoi {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "response_content", columnDefinition = "TEXT", nullable = false)
    private String response_content;

    @Column(name = "sent_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime sent_at;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yeu_cau")
    private YeuCau id_yeu_cau;
}
