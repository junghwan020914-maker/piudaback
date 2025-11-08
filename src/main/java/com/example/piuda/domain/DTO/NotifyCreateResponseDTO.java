package com.example.piuda.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotifyCreateResponseDTO {
    private Long notifyId;
    private Long pinId;
    private String status;
    private List<String> photoUrls;
}
