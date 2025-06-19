package com.vnpt.hethonghotro.service;

import com.vnpt.hethonghotro.dto.ThaoTacRequest;
import com.vnpt.hethonghotro.entity.*;
import com.vnpt.hethonghotro.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YeuCauService {

    //<editor-fold desc="Repositories and Constants">
    private final YeuCauRepository yeuCauRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThaoTacRepository thaoTacRepository;
    private final PhanHoiRepository phanHoiRepository;
    private final XuLyYeuCauRepository xuLyYeuCauRepository;
    private final CoThaoTacRepository coThaoTacRepository;

    private static final String TIEP_NHAN_ID = "550e8400-e29b-41d4-a716-000000000600";
    private static final String BO_QUA_ID = "550e8400-e29b-41d4-a716-000000000601";
    private static final String PHAN_HOI_ID = "550e8400-e29b-41d4-a716-000000000602";
    private static final String KET_THUC_ID = "550e8400-e29b-41d4-a716-000000000603"; // ID cho thao tác "Kết thúc"
    //</editor-fold>

    /**
     * Enum để định nghĩa các trạng thái của một yêu cầu.
     * Mới: Chưa có ai xử lý.
     * Đang xử lý: Đã được tiếp nhận.
     * Đã đóng: Đã bị bỏ qua hoặc đã kết thúc.
     */
    private enum TrangThaiYeuCau {
        MOI,
        DANG_XU_LY,
        DA_DONG
    }

    @Transactional
    public void xuLyHanhDong(String requestId, ThaoTacRequest request, String username) {
        YeuCau yeuCau = yeuCauRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy yêu cầu: " + requestId));
        NguoiDung nguoiDung = nguoiDungRepository.findById(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng: " + username));

        // 1. Lấy lịch sử thao tác để xác định trạng thái hiện tại
        List<CoThaoTac> history = coThaoTacRepository.findByYeuCau_Id(requestId);
        TrangThaiYeuCau currentState = xacDinhTrangThaiHienTai(history);

        // 2. Kiểm tra xem hành động có hợp lệ với trạng thái hiện tại không
        String hanhDongMoi = request.getThao_tac().toLowerCase();
        kiemTraHanhDongHopLe(hanhDongMoi, currentState);

        // 3. Thực hiện hành động nếu hợp lệ
        switch (hanhDongMoi) {
            case "tiep_nhan" -> handleTiepNhan(yeuCau, nguoiDung);
            case "bo_qua" -> handleBoQua(yeuCau, nguoiDung);
            case "phan_hoi" -> {
                if (request.getNoi_dung_phan_hoi() == null || request.getNoi_dung_phan_hoi().isBlank()) {
                    throw new IllegalArgumentException("Nội dung phản hồi không được để trống");
                }
                handlePhanHoi(yeuCau, nguoiDung, request.getNoi_dung_phan_hoi());
            }
            case "ket_thuc" -> handleKetThuc(yeuCau, nguoiDung);
            default -> throw new IllegalArgumentException("Hành động không xác định: " + hanhDongMoi);
        }
    }

    /**
     * Xác định trạng thái của yêu cầu dựa trên lịch sử các thao tác.
     */
    private TrangThaiYeuCau xacDinhTrangThaiHienTai(List<CoThaoTac> history) {
        if (history.isEmpty()) {
            return TrangThaiYeuCau.MOI;
        }

        Set<String> thaoTacIds = history.stream()
                .map(coThaoTac -> coThaoTac.getId_thao_tac().getId())
                .collect(Collectors.toSet());

        if (thaoTacIds.contains(BO_QUA_ID) || thaoTacIds.contains(KET_THUC_ID)) {
            return TrangThaiYeuCau.DA_DONG;
        }

        if (thaoTacIds.contains(TIEP_NHAN_ID)) {
            return TrangThaiYeuCau.DANG_XU_LY;
        }

        // Trường hợp mặc định (mặc dù khó xảy ra nếu logic đúng)
        return TrangThaiYeuCau.MOI;
    }

    /**
     * Kiểm tra tính hợp lệ của một hành động dựa trên trạng thái hiện tại.
     */
    private void kiemTraHanhDongHopLe(String hanhDongMoi, TrangThaiYeuCau currentState) {
        switch (currentState) {
            case MOI:
                if (!hanhDongMoi.equals("tiep_nhan") && !hanhDongMoi.equals("bo_qua")) {
                    throw new IllegalStateException("Yêu cầu mới chỉ có thể được 'Tiếp nhận' hoặc 'Bỏ qua'.");
                }
                break;
            case DANG_XU_LY:
                if (!hanhDongMoi.equals("phan_hoi") && !hanhDongMoi.equals("ket_thuc")) {
                    throw new IllegalStateException("Yêu cầu đang xử lý chỉ có thể 'Phản hồi' hoặc 'Kết thúc'.");
                }
                break;
            case DA_DONG:
                throw new IllegalStateException("Yêu cầu này đã được đóng và không thể thực hiện thêm thao tác.");
        }
    }

    //<editor-fold desc="Các hàm xử lý hành động cụ thể">
    private void handleTiepNhan(YeuCau yeuCau, NguoiDung nguoiDung) {
        // Gán quyền xử lý yêu cầu cho người dùng
        XuLyYeuCau xuLy = new XuLyYeuCau();
        xuLy.setId_yeu_cau(yeuCau);
        xuLy.setUsername(nguoiDung);
        xuLyYeuCauRepository.save(xuLy);
        // Ghi nhận lại thao tác
        ghiNhanThaoTac(yeuCau, TIEP_NHAN_ID);
    }

    private void handleBoQua(YeuCau yeuCau, NguoiDung nguoiDung) {
        XuLyYeuCau boQua = new XuLyYeuCau();
        boQua.setId_yeu_cau(yeuCau);
        boQua.setUsername(nguoiDung);
        ghiNhanThaoTac(yeuCau, BO_QUA_ID);
    }

    private void handlePhanHoi(YeuCau yeuCau, NguoiDung nguoiDung, String noiDung) {
        // Tạo và lưu phản hồi
        XuLyYeuCau phanHoiYeuCau = new XuLyYeuCau();
        phanHoiYeuCau.setUsername(nguoiDung);
        PhanHoi phanHoi = new PhanHoi();
        phanHoi.setId(UUID.randomUUID().toString());
        phanHoi.setResponse_content(noiDung);
        phanHoi.setId_yeu_cau(yeuCau);
        phanHoiRepository.save(phanHoi);
        // Ghi nhận lại thao tác phản hồi
        ghiNhanThaoTac(yeuCau, PHAN_HOI_ID);
    }

    private void handleKetThuc(YeuCau yeuCau, NguoiDung nguoiDung) {
        XuLyYeuCau ketThuc = new XuLyYeuCau();
        ketThuc.setId_yeu_cau(yeuCau);
        ketThuc.setUsername(nguoiDung);
        ghiNhanThaoTac(yeuCau, KET_THUC_ID);
    }

    private void ghiNhanThaoTac(YeuCau yeuCau, String thaoTacId) {
        ThaoTac thaoTac = thaoTacRepository.findById(thaoTacId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy thao tác: " + thaoTacId));
        CoThaoTac coThaoTac = new CoThaoTac();
        coThaoTac.setYeuCau(yeuCau);
        coThaoTac.setId_thao_tac(thaoTac);
        coThaoTacRepository.save(coThaoTac);
    }
    //</editor-fold>
}
