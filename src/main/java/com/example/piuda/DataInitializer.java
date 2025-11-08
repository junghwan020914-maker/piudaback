package com.example.piuda;

import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.Pin.PinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PinRepository pinRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
    if (pinRepository.count() == 0) {
        Pin pin1 = Pin.builder()
            .pinX(37.5665) // Seoul City Hall
            .pinY(126.9780)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.BLUE)
            .build();

        Pin pin2 = Pin.builder()
            .pinX(35.1796) // Busan City Hall
            .pinY(129.0756)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.SOUTH_SEA)
            .pinColor(Pin.PinColor.RED)
            .build();

        Pin pin3 = Pin.builder()
            .pinX(33.4996) // Jeju City Hall
            .pinY(126.5312)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.JEJU)
            .build();

        Pin pin4 = Pin.builder()
            .pinX(37.4563) // Incheon
            .pinY(126.7052)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.WHITE)
            .build();

        Pin pin5 = Pin.builder()
            .pinX(35.8714) // Daegu
            .pinY(128.6014)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.EAST_SEA)
            .pinColor(Pin.PinColor.BLUE)
            .build();

        Pin pin6 = Pin.builder()
            .pinX(36.3504) // Daejeon
            .pinY(127.3845)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.RED)
            .build();

        Pin pin7 = Pin.builder()
            .pinX(35.5384) // Ulsan
            .pinY(129.3114)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.EAST_SEA)
            .pinColor(Pin.PinColor.WHITE)
            .build();

        Pin pin8 = Pin.builder()
            .pinX(37.2636) // Suwon
            .pinY(127.0286)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.BLUE)
            .build();

        Pin pin9 = Pin.builder()
            .pinX(36.6357) // Cheongju
            .pinY(127.4917)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.RED)
            .build();

        Pin pin10 = Pin.builder()
            .pinX(37.8813) // Chuncheon
            .pinY(127.7298)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.EAST_SEA)
            .pinColor(Pin.PinColor.WHITE)
            .build();

        Pin pin11 = Pin.builder()
            .pinX(37.7519) // Uijeongbu
            .pinY(127.0776)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.BLUE)
            .build();

        Pin pin12 = Pin.builder()
            .pinX(37.3943) // Seongnam
            .pinY(127.1107)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.RED)
            .build();

        Pin pin13 = Pin.builder()
            .pinX(37.5663) // Goyang
            .pinY(126.9996)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.WHITE)
            .build();

        Pin pin14 = Pin.builder()
            .pinX(37.4562) // Ansan
            .pinY(126.7052)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.BLUE)
            .build();

        Pin pin15 = Pin.builder()
            .pinX(37.3219) // Anyang
            .pinY(126.8309)
            .pinCreatedAt(LocalDateTime.now())
            .region(Pin.Region.WEST_SEA)
            .pinColor(Pin.PinColor.RED)
            .build();

        pinRepository.saveAll(Arrays.asList(
        pin1, pin2, pin3, pin4, pin5, pin6, pin7, pin8, pin9, pin10,
        pin11, pin12, pin13, pin14, pin15
        ));
        System.out.println("Default pin data initialized with more entries.");
    }
    }
}
