package com.example.piuda.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * 후기(Report) 작성 요청 DTO
 * 기존 trashPlastic / trashVinyl 명칭을 엔티티(Trash)의 필드명과 동일하게 맞췄습니다.
 * 필요시 프론트에서 기존 이름을 쓴다면 매핑 단계에서 변환하도록 Service에 주석을 남기세요.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportRequestDTO {
    private String reportName;            // 작성자
    private Integer reportPeople;         // 활동 인원수
    private String reportTitle;           // 제목
    private LocalDate reportDate;         // 활동 날짜 (yyyy-MM-dd)
    private String reportDetailLocation;  // 상세 위치 (선택)
    private String reportContent;         // 정성 기록 (선택)
    private String turnstileToken;

    // 핀 좌표
    private Double pinX;                  // 위도
    private Double pinY;                  // 경도


    // 전체 쓰레기 양 (kg, L 분리)
    private Double trashKg;               // kg 단위 전체 무게
    private Double trashL;                // L 단위 전체 부피

    // 세부 쓰레기 분류 - Trash 엔티티 필드명과 동일하게 수정
    private Integer trashPet;             // 페트병 개수
    private Integer trashBag;             // 비닐봉지 개수
    private Integer trashNet;             // 그물 개수 (선택)
    private Integer trashGlass;           // 유리 개수
    private Integer trashCan;             // 캔 개수
    private Integer trashRope;            // 로프 개수 (선택)
    private Integer trashCloth;           // 천/의류 (선택)
    private Integer trashElec;            // 전자제품 (선택)
    private Integer trashEtc;             // 기타 (선택)
}