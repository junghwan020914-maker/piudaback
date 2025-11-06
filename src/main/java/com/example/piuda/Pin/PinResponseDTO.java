package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PinResponseDTO {
    private Long pinId;
    private Double pinX;
    private Double pinY;
    private LocalDateTime pinCreatedAt;
    private Pin.PinColor pinColor;
    private Pin.Region region;
    private String organizationName;
    // 핀에 참여한 모든 단체명 (중복 제거)
    private List<String> organizationNames;
    private Double totalTrashKg;
    private Double totalTrashL;

    public PinResponseDTO(Pin pin, String organizationName, Double totalTrashKg, Double totalTrashL) {
        this.pinId = pin.getPinId();
        this.pinX = pin.getPinX();
        this.pinY = pin.getPinY();
        this.pinCreatedAt = pin.getPinCreatedAt();
        this.pinColor = pin.getPinColor();
        this.region = pin.getRegion();
        this.organizationName = organizationName;
        this.totalTrashKg = totalTrashKg;
        this.totalTrashL = totalTrashL;
    }

    // 다중 단체명을 담는 생성자 (권장)
    public PinResponseDTO(Pin pin, List<String> organizationNames, Double totalTrashKg, Double totalTrashL) {
        this.pinId = pin.getPinId();
        this.pinX = pin.getPinX();
        this.pinY = pin.getPinY();
        this.pinCreatedAt = pin.getPinCreatedAt();
        this.pinColor = pin.getPinColor();
        this.region = pin.getRegion();
        this.organizationNames = organizationNames;
        this.totalTrashKg = totalTrashKg;
        this.totalTrashL = totalTrashL;
    }
}
