package com.vnpt.hethonghotro.repository;

import com.vnpt.hethonghotro.entity.CoThaoTac;
import com.vnpt.hethonghotro.entity.pks.CoThaoTacPK;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoThaoTacRepository extends JpaRepository<CoThaoTac, CoThaoTacPK> {
    List<CoThaoTac> findByYeuCau_Id(String yeuCauId);
}