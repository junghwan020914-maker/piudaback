package com.example.piuda.Pin;

import com.example.piuda.domain.DTO.PinResponseDTO;
import com.example.piuda.domain.Entity.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController //Controller + Reponsebody
@RequestMapping("/api/pins")
@RequiredArgsConstructor
public class PinController {
    private final PinService pinService;

    @GetMapping
    public List<PinResponseDTO> getAllPins() {
        // 전체 핀 + 집계 정보를 한번에 내려주어 프론트가 클라이언트 사이드 필터링 가능하게 함
        return pinService.getAllPinsForClient();
    }

    // 특정 핀 상세 정보: 최근 활동일, 누적 수거량, 활동 횟수, 참여 단체, 후기 목록 요약
    @GetMapping("/{pinId}/details")
    public PinResponseDTO getPinDetails(@PathVariable Long pinId) {
        return pinService.getPinDetails(pinId);
    }
}
