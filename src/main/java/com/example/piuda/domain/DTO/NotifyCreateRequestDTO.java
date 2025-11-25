package com.example.piuda.domain.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotifyCreateRequestDTO {
    private Double x;           // 위도
    private Double y;           // 경도
    private String content;     // 제보 내용 (선택 가능)
    private String turnstileToken;

    // getter/setter
    public String getTurnstileToken() {
        return turnstileToken;
    }

    public void setTurnstileToken(String turnstileToken) {
        this.turnstileToken = turnstileToken;
    }
}
