package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
    public List<Pin> getAllPins() {
        return pinService.getAllPins();
    }

    @GetMapping("/filter")
    public List<PinResponseDTO> getPins(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            // 쉼표(,)로 구분된 단체명 목록. 예) organizationNames=단체A,단체B
            @RequestParam(required = false) String organizationNames,
            @RequestParam(required = false) Pin.Region region,
            @RequestParam(required = false) Double minKg,
            @RequestParam(required = false) Double minL) {
        List<String> orgList = null;
        if (organizationNames != null && !organizationNames.isBlank()) {
            orgList = Arrays.stream(organizationNames.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .collect(Collectors.toList());
        }
        return pinService.getFilteredPins(startDate, endDate, orgList, region, minKg, minL);
    }
}
