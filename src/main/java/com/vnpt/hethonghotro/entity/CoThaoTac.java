package com.vnpt.hethonghotro.entity;

import com.vnpt.hethonghotro.entity.pks.CoThaoTacPK;
import jakarta.persistence.*;
import lombok.Data;
@Entity @Table(name = "co_thao_tac") @Data @IdClass(CoThaoTacPK.class)
public class CoThaoTac {
    @Id @ManyToOne @JoinColumn(name = "id_yeu_cau", referencedColumnName = "id")
    private YeuCau yeuCau;
    @Id @ManyToOne @JoinColumn(name = "id_thao_tac", referencedColumnName = "id")
    private ThaoTac id_thao_tac;
}