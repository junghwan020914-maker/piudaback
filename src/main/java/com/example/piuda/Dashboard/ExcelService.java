package com.example.piuda.Dashboard;

import com.example.piuda.Org.OrgRepository;
import com.example.piuda.Report.ReportRepository;
import com.example.piuda.User.UserRepository;
import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.Report;
import com.example.piuda.domain.Entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    private final ReportRepository reportRepository;
    private final OrgRepository orgRepository;
    private final UserRepository userRepository;

    /**
     * 모든 후기 데이터를 엑셀 파일로 생성
     * @return 엑셀 파일의 바이트 배열
     */
    @Transactional(readOnly = true)
    public byte[] generateReportsExcel() throws IOException {
        // 모든 후기 조회 (최신순)
        List<Report> reports = reportRepository.findAllByOrderByReportDateDesc();

        // 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("후기 목록");

        // 헤더 스타일 설정
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {
            "후기 ID", "후기 제목", "작성자 분류", "작성 단체", "작성자명", 
            "활동 날짜", "참여 인원", "상세 위치", "내용",
            "X 좌표", "Y 좌표",
            "쓰레기 총 무게(kg)", "쓰레기 총 부피(L)",
            "페트병", "비닐봉지", "그물", "유리", "캔", "로프", "천/의류", "전자제품", "기타"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Report report : reports) {
            Row row = sheet.createRow(rowNum++);

            int colNum = 0;
            
            // 후기 ID
            row.createCell(colNum++).setCellValue(report.getReportId());
            
            // 후기 제목
            row.createCell(colNum++).setCellValue(report.getReportTitle());
            
            // 작성자 분류
            row.createCell(colNum++).setCellValue(report.getWriterType() != null ? 
                report.getWriterType().name() : "");
            
            // 작성 단체
            row.createCell(colNum++).setCellValue(report.getOrg() != null ? 
                report.getOrg().getOrgName() : "");
            
            // 작성자명
            row.createCell(colNum++).setCellValue(report.getWriter() != null ? 
                report.getWriter().getUserName() : "");
            
            // 작성 날짜
            row.createCell(colNum++).setCellValue(report.getReportDate() != null ? 
                report.getReportDate().format(dateFormatter) : "");
            
            // 참여 인원
            row.createCell(colNum++).setCellValue(report.getReportPeople() != null ? 
                report.getReportPeople() : 0);
            
            // 상세 위치
            row.createCell(colNum++).setCellValue(report.getReportDetailLocation() != null ? 
                report.getReportDetailLocation() : "");
            
            // 내용
            row.createCell(colNum++).setCellValue(report.getReportContent() != null ? 
                report.getReportContent() : "");
            
            // X, Y 좌표 (Pin에서 가져오기)
            if (report.getPin() != null) {
                row.createCell(colNum++).setCellValue(report.getPin().getPinX());
                row.createCell(colNum++).setCellValue(report.getPin().getPinY());
            } else {
                row.createCell(colNum++).setCellValue("");
                row.createCell(colNum++).setCellValue("");
            }
            
            // Trash 정보
            if (report.getTrash() != null) {
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashKg() != null ? 
                    report.getTrash().getTrashKg() : 0.0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashL() != null ? 
                    report.getTrash().getTrashL() : 0.0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashPet() != null ? 
                    report.getTrash().getTrashPet() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashBag() != null ? 
                    report.getTrash().getTrashBag() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashNet() != null ? 
                    report.getTrash().getTrashNet() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashGlass() != null ? 
                    report.getTrash().getTrashGlass() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashCan() != null ? 
                    report.getTrash().getTrashCan() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashRope() != null ? 
                    report.getTrash().getTrashRope() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashCloth() != null ? 
                    report.getTrash().getTrashCloth() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashElec() != null ? 
                    report.getTrash().getTrashElec() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashEtc() != null ? 
                    report.getTrash().getTrashEtc() : 0);
            } else {
                // Trash 정보가 없으면 빈 값으로 채우기
                for (int i = 0; i < 11; i++) {
                    row.createCell(colNum++).setCellValue("");
                }
            }
        }

        // 열 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
            // 최소 너비 설정
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * 특정 단체가 작성한 후기 데이터를 엑셀 파일로 생성
     * @param email 로그인한 사용자의 이메일
     * @return 엑셀 파일의 바이트 배열
     */
    @Transactional(readOnly = true)
    public byte[] generateOrgReportsExcel(String email) throws IOException {
        // 1. 이메일로 User 조회
        User user = userRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        
        // 2. User로 Org 조회
        Org org = orgRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("단체 정보를 찾을 수 없습니다."));
        
        // 3. 해당 단체가 작성한 후기 조회 (최신순)
        List<Report> reports = reportRepository.findByOrgOrderByReportDateDesc(org);

        // 워크북 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("후기 목록");

        // 헤더 스타일 설정
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 헤더 행 생성
        Row headerRow = sheet.createRow(0);
        String[] columns = {
            "후기 ID", "후기 제목", "작성 단체", "작성자명", 
            "활동 날짜", "참여 인원", "상세 위치", "내용",
            "X 좌표", "Y 좌표",
            "쓰레기 총 무게(kg)", "쓰레기 총 부피(L)",
            "페트병", "비닐봉지", "그물", "유리", "캔", "로프", "천/의류", "전자제품", "기타"
        };

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 데이터 행 생성
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Report report : reports) {
            Row row = sheet.createRow(rowNum++);

            int colNum = 0;
            
            // 후기 ID
            row.createCell(colNum++).setCellValue(report.getReportId());
            
            // 후기 제목
            row.createCell(colNum++).setCellValue(report.getReportTitle());
            
            // 작성 단체
            row.createCell(colNum++).setCellValue(report.getOrg() != null ? 
                report.getOrg().getOrgName() : "");
            
            // 작성자명
            row.createCell(colNum++).setCellValue(report.getWriter() != null ? 
                report.getWriter().getUserName() : "");
            
            // 작성 날짜
            row.createCell(colNum++).setCellValue(report.getReportDate() != null ? 
                report.getReportDate().format(dateFormatter) : "");
            
            // 참여 인원
            row.createCell(colNum++).setCellValue(report.getReportPeople() != null ? 
                report.getReportPeople() : 0);
            
            // 상세 위치
            row.createCell(colNum++).setCellValue(report.getReportDetailLocation() != null ? 
                report.getReportDetailLocation() : "");
            
            // 내용
            row.createCell(colNum++).setCellValue(report.getReportContent() != null ? 
                report.getReportContent() : "");
            
            // X, Y 좌표 (Pin에서 가져오기)
            if (report.getPin() != null) {
                row.createCell(colNum++).setCellValue(report.getPin().getPinX());
                row.createCell(colNum++).setCellValue(report.getPin().getPinY());
            } else {
                row.createCell(colNum++).setCellValue("");
                row.createCell(colNum++).setCellValue("");
            }
            
            // Trash 정보
            if (report.getTrash() != null) {
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashKg() != null ? 
                    report.getTrash().getTrashKg() : 0.0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashL() != null ? 
                    report.getTrash().getTrashL() : 0.0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashPet() != null ? 
                    report.getTrash().getTrashPet() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashBag() != null ? 
                    report.getTrash().getTrashBag() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashNet() != null ? 
                    report.getTrash().getTrashNet() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashGlass() != null ? 
                    report.getTrash().getTrashGlass() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashCan() != null ? 
                    report.getTrash().getTrashCan() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashRope() != null ? 
                    report.getTrash().getTrashRope() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashCloth() != null ? 
                    report.getTrash().getTrashCloth() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashElec() != null ? 
                    report.getTrash().getTrashElec() : 0);
                row.createCell(colNum++).setCellValue(report.getTrash().getTrashEtc() != null ? 
                    report.getTrash().getTrashEtc() : 0);
            } else {
                // Trash 정보가 없으면 빈 값으로 채우기
                for (int i = 0; i < 11; i++) {
                    row.createCell(colNum++).setCellValue("");
                }
            }
        }

        // 열 너비 자동 조정
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
            // 최소 너비 설정
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 3000));
        }

        // 바이트 배열로 변환
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }
}
