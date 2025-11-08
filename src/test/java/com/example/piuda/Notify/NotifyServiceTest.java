package com.example.piuda.Notify;

import com.example.piuda.Pin.PinService;
import com.example.piuda.domain.DTO.NotifyCreateRequestDTO;
import com.example.piuda.domain.Entity.Notify;
import com.example.piuda.domain.Entity.NotifyPhoto;
import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.storage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class NotifyServiceTest {

    private NotifyRepository notifyRepository;
    private PinService pinService;
    private com.example.piuda.NotifyPhoto.NotifyPhotoRepository notifyPhotoRepository;
    private StorageService storageService;
    private NotifyService notifyService;

    @BeforeEach
    void setup() {
        notifyRepository = mock(NotifyRepository.class);
        pinService = mock(PinService.class);
        notifyPhotoRepository = mock(com.example.piuda.NotifyPhoto.NotifyPhotoRepository.class);
        storageService = mock(StorageService.class);
        notifyService = new NotifyService(notifyRepository, pinService, notifyPhotoRepository, storageService);
    }

    @Test
    void createAndAccept_createsRedPinAndStoresPhotos() {
        // given
        NotifyCreateRequestDTO dto = mock(NotifyCreateRequestDTO.class);
        when(dto.getX()).thenReturn(127.11);
        when(dto.getY()).thenReturn(37.44);
        when(dto.getContent()).thenReturn("제보 내용");

        Pin pin = Pin.builder().pinId(10L).pinX(127.11).pinY(37.44).pinColor(Pin.PinColor.RED).build();
        when(pinService.addPinFromNotifyAndGet(anyDouble(), anyDouble(), anyDouble())).thenReturn(Optional.of(pin));

        // 사진 모킹
        MultipartFile photo = mock(MultipartFile.class);
        when(storageService.upload(eq("notifies"), any(MultipartFile.class))).thenReturn("https://cdn.example.com/notifies/a.jpg");

        // 저장시 Notify 엔티티 반환 모킹
        when(notifyRepository.save(any(Notify.class))).thenAnswer(invocation -> {
            Notify n = invocation.getArgument(0);
            // ID 세팅 시뮬레이션
            return Notify.builder()
                    .notifyId(99L)
                    .notifyX(n.getNotifyX())
                    .notifyY(n.getNotifyY())
                    .notifyCreatedAt(n.getNotifyCreatedAt())
                    .notifyContent(n.getNotifyContent())
                    .notifyStatus(n.getNotifyStatus())
                    .pin(n.getPin())
                    .build();
        });

        // when
        Long id = notifyService.createAndAccept(dto, List.of(photo));

        // then
        assertThat(id).isEqualTo(99L);
        verify(storageService, times(1)).upload(eq("notifies"), eq(photo));
        ArgumentCaptor<NotifyPhoto> captor = ArgumentCaptor.forClass(NotifyPhoto.class);
        verify(notifyPhotoRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getNphotoPath()).contains("https://cdn.example.com/notifies");
        assertThat(captor.getValue().getNotify()).isNotNull();
    }
}
