package com.tuandatcoder.trekkerbackend.service;

import com.tuandatcoder.trekkerbackend.dto.ApiResponse;
import com.tuandatcoder.trekkerbackend.dto.notification.request.MarkAsReadRequestDTO;
import com.tuandatcoder.trekkerbackend.dto.notification.response.NotificationResponseDTO;
import com.tuandatcoder.trekkerbackend.entity.Account;
import com.tuandatcoder.trekkerbackend.entity.Notification;
import com.tuandatcoder.trekkerbackend.enums.NotificationTypeEnum;
import com.tuandatcoder.trekkerbackend.enums.ReferenceTypeEnum;
import com.tuandatcoder.trekkerbackend.exception.ApiException;
import com.tuandatcoder.trekkerbackend.exception.ErrorCode;
import com.tuandatcoder.trekkerbackend.helper.ActivityLogger;
import com.tuandatcoder.trekkerbackend.mapper.NotificationMapper;
import com.tuandatcoder.trekkerbackend.repository.NotificationRepository;
import com.tuandatcoder.trekkerbackend.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private AccountUtils accountUtils;
    @Autowired private NotificationMapper notificationMapper;
    @Autowired private ActivityLogger activityLogger;

    private Account getCurrentAccount() {
        Account acc = accountUtils.getCurrentAccount();
        if (acc == null) throw new ApiException("Login required", ErrorCode.UNAUTHENTICATED);
        return acc;
    }

    // Tạo thông báo (gọi từ các service khác)
    @Transactional
    public void createNotification(Account recipient, Account sender,
                                   NotificationTypeEnum type, ReferenceTypeEnum refType,
                                   Long refId, String title, String message) {

        if (recipient.getId().equals(sender.getId())) return; // Không tự thông báo mình

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .referenceType(refType)
                .referenceId(refId)
                .title(title)
                .message(message)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    // Lấy danh sách thông báo
    public ApiResponse<List<NotificationResponseDTO>> getMyNotifications() {
        Account current = getCurrentAccount();
        List<Notification> notifications = notificationRepository.findByRecipientId(current.getId());
        return new ApiResponse<>(200, "Notifications retrieved", notificationMapper.toDtoList(notifications));
    }

    // Lấy số lượng chưa đọc
    public ApiResponse<Long> getUnreadCount() {
        Account current = getCurrentAccount();
        long count = notificationRepository.countByRecipientIdAndIsReadFalseAndDeletedAtIsNull(current.getId());
        return new ApiResponse<>(200, "Unread count", count);
    }

    // Đánh dấu đã đọc
    @Transactional
    public ApiResponse<String> markAsRead(MarkAsReadRequestDTO dto) {
        Account current = getCurrentAccount();

        if (dto.getNotificationIds() == null || dto.getNotificationIds().isEmpty()) {
            return new ApiResponse<>(200, "No notifications to mark", null);
        }

        dto.getNotificationIds().forEach(id -> {
            Notification n = notificationRepository.findById(id)
                    .orElse(null);
            if (n != null && n.getRecipient().getId().equals(current.getId()) && !n.isRead()) {
                n.setRead(true);
                n.setReadAt(LocalDateTime.now());
                notificationRepository.save(n);
            }
        });

        return new ApiResponse<>(200, "Notifications marked as read", null);
    }

    // Xóa thông báo
    @Transactional
    public ApiResponse<String> deleteNotification(Long id) {
        Account current = getCurrentAccount();

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Notification not found", ErrorCode.NOT_FOUND));

        if (!notification.getRecipient().getId().equals(current.getId())) {
            throw new ApiException("You can only delete your own notifications", ErrorCode.FORBIDDEN);
        }

        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return new ApiResponse<>(200, "Notification deleted", null);
    }
}