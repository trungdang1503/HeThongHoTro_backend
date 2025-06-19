package com.vnpt.hethonghotro.entity.pks;

import java.io.Serializable;
import lombok.*;
@Data @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class CoThaoTacPK implements Serializable {
    private String yeuCau;
    private String id_thao_tac;
}