package com.vnpt.hethonghotro.repository;

import com.vnpt.hethonghotro.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {
    Optional<NguoiDung> findByUsername(String username);
}