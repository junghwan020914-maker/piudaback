package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PinService {
	private final PinRepository pinRepository;

	public List<Pin> getAllPins() {
		return pinRepository.findAll();
	}

	// 일정 거리 내 중복 체크 (예: 50m)
	private boolean isPinNearby(double x, double y, double distanceMeters) {
		List<Pin> pins = pinRepository.findAll();
		for (Pin pin : pins) {
			double dx = pin.getPinX() - x;
			double dy = pin.getPinY() - y;
			double dist = Math.sqrt(dx * dx + dy * dy) * 111_000; // 위도/경도 -> m 변환(간략)
			if (dist < distanceMeters) return true;
		}
		return false;
	}

	// Notify에서 ACCEPT인 경우 핀 추가
	public boolean addPinFromNotify(double x, double y) {
		if (isPinNearby(x, y, 50)) return false;
		Pin pin = Pin.builder()
				.pinX(x)
				.pinY(y)
				.pinCreatedAt(java.time.LocalDateTime.now())
				.pinColor(Pin.PinColor.RED)
				.build();
		pinRepository.save(pin);
		return true;
	}

	// 일정 거리 내 중복이 없으면 Pin을 생성하고 생성된 Pin을 반환
	public Optional<Pin> createPinIfNotNearby(double x, double y, double distanceMeters) {
		if (isPinNearby(x, y, distanceMeters)) {
			return Optional.empty();
		}
		Pin pin = Pin.builder()
				.pinX(x)
				.pinY(y)
				.pinCreatedAt(java.time.LocalDateTime.now())
				.pinColor(Pin.PinColor.RED)
				.build();
		Pin saved = pinRepository.save(pin);
		return Optional.of(saved);
	}
	// 예약 시 핀 색상 파란색으로 변경
	public boolean setPinColorBlue(Long pinId) {
		return pinRepository.findById(pinId).map(pin -> {
			Pin updated = Pin.builder()
				.pinId(pin.getPinId())
				.pinX(pin.getPinX())
				.pinY(pin.getPinY())
				.pinCreatedAt(pin.getPinCreatedAt())
				.pinColor(Pin.PinColor.BLUE)
				.build();
			pinRepository.save(updated);
			return true;
		}).orElse(false);
	}
}
