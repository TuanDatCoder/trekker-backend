package com.tuandatcoder.trekkerbackend.dto.notification.response;

import com.tuandatcoder.trekkerbackend.enums.NotificationTypeEnum;
import com.tuandatcoder.trekkerbackend.enums.ReferenceTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    private Long id;

    private Long senderId;
    private String senderUsername;
    private String senderName;
    private String senderPicture;

    private NotificationTypeEnum type;
    private ReferenceTypeEnum referenceType;
    private Long referenceId;

    private String title;
    private String message;

    private boolean isRead;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}