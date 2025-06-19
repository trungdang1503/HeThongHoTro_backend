package com.vnpt.hethonghotro.service;

import com.vnpt.hethonghotro.entity.NguoiDung;
import com.vnpt.hethonghotro.entity.VaiTro;
import com.vnpt.hethonghotro.repository.NguoiDungRepository;
import com.vnpt.hethonghotro.repository.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository; // Thêm repository này

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        NguoiDung nguoiDung = nguoiDungRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));

        // *** THAY ĐỔI QUAN TRỌNG: Lấy vai trò từ CSDL ***
        List<VaiTro> roles = vaiTroRepository.findRolesByUsername(username);
        List<GrantedAuthority> authorities = roles.stream()
                // Thêm tiền tố "ROLE_" theo quy ước của Spring Security
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()))
                .collect(Collectors.toList());

        // Trả về đối tượng UserDetails với ĐẦY ĐỦ QUYỀN
        return new User(nguoiDung.getUsername(), nguoiDung.getPassword(), authorities);
    }
}
