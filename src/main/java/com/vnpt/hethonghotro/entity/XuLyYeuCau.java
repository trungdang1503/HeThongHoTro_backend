package com.vnpt.hethonghotro.entity;

import com.vnpt.hethonghotro.entity.pks.XuLyYeuCauPK;
import jakarta.persistence.*;
import lombok.Data;
@Entity @Table(name = "xu_ly_yeu_cau") @Data @IdClass(XuLyYeuCauPK.class)
public class XuLyYeuCau {
    @Id @ManyToOne @JoinColumn(name = "username", referencedColumnName = "username")
    private NguoiDung username;
    @Id @ManyToOne @JoinColumn(name = "id_yeu_cau", referencedColumnName = "id")
    private YeuCau id_yeu_cau;
}