package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController //Controller + Reponsebody
@RequestMapping("/api/pins")
@RequiredArgsConstructor
public class PinController {
	private final PinService pinService;

	@GetMapping
	public List<Pin> getAllPins() {
		return pinService.getAllPins();
	}

	// Notify에서 ACCEPT인 경우 핀 추가 API
	@GetMapping("/add-from-notify")
	public String addPinFromNotify(double x, double y) {
		boolean result = pinService.addPinFromNotify(x, y);
		return result ? "핀 추가 성공" : "근처에 이미 핀이 있습니다";
	}
}
