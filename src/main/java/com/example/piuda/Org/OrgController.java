package com.example.piuda.Org;

import com.example.piuda.Dashboard.AccumulationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrgController {

    private final AccumulationService accumulationService;

    /**
     * 단체 누적 데이터 수동 업데이트
     * 테스트 또는 즉시 업데이트가 필요한 경우 사용
     */
    @PostMapping("/accum/update")
    public ResponseEntity<String> updateOrgAccumulation() {
        accumulationService.updateOrgAccumulation();
        return ResponseEntity.ok("단체 누적 데이터가 업데이트되었습니다.");
    }
}
