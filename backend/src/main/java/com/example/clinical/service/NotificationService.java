package com.example.clinical.service;

import com.example.clinical.domain.entity.Notification;
import com.example.clinical.domain.entity.User;
import com.example.clinical.domain.enums.NotificationStatus;
import com.example.clinical.domain.enums.NotificationType;
import com.example.clinical.repository.NotificationRepository;
import com.example.clinical.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Transactional
    public Notification create(UUID recipientId, NotificationType type) {
        User recipient = userRepository.findById(recipientId).orElseThrow(() -> new IllegalArgumentException("Recipient not found"));
        Notification n = new Notification();
        n.setRecipient(recipient);
        n.setNotificationType(type);
        n.setStatus(NotificationStatus.UNREAD);
        n.setCreatedAt(ZonedDateTime.now());
        return notificationRepository.save(n);
    }

    @Transactional(readOnly = true)
    public List<Notification> list(UUID recipientId) { return notificationRepository.findByRecipient_IdOrderByCreatedAtDesc(recipientId); }

    @Transactional
    public Notification mark(UUID id, NotificationStatus status) {
        Notification n = notificationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        n.setStatus(status);
        return notificationRepository.save(n);
    }
}
