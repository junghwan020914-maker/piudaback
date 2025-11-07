package com.example.piuda.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class ReportRequestDTO {
    private String reportName;        // 작성자
    private Integer reportPeople;     // 활동인원수
    private String reportTitle;       // 제목
    private LocalDate reportDate;     // 날짜
    private String reportDetailLocation; // 상세위치 (선택)
    private String reportContent;     // 정성기록 (선택)
    private Double pinX;              // 위도
    private Double pinY;              // 경도
    private Double trashTotal;        // 전체 쓰레기 양
    private String trashUnit;         // 단위 (kg 또는 L)
    
    // Trash 엔티티의 실제 필드명에 맞춤 (선택적)
    private Integer trashPlastic;     // PET (트래시펫)
    private Integer trashVinyl;       // 비닐봉지 (트래시백)
    private Integer trashGlass;       // 유리 개수 (선택)
    private Integer trashCan;         // 캔 개수 (선택)
}