package com.example.piuda.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NotifyCreateRequestDTO {
    private Double x;           // 위도
    private Double y;           // 경도
    private String content;     // 제보 내용 (선택 가능)
}
