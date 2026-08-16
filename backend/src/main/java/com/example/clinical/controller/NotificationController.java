package com.example.clinical.controller;

import com.example.clinical.domain.entity.Notification;
import com.example.clinical.domain.enums.NotificationStatus;
import com.example.clinical.domain.enums.NotificationType;
import com.example.clinical.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @PostMapping("/users/{recipientId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Notification create(@PathVariable UUID recipientId, @RequestParam NotificationType type) { return service.create(recipientId, type); }

    @GetMapping("/users/{recipientId}")
    public List<Notification> list(@PathVariable UUID recipientId) { return service.list(recipientId); }

    @PatchMapping("/{notificationId}")
    public Notification updateStatus(@PathVariable UUID notificationId, @RequestParam NotificationStatus status) { return service.mark(notificationId, status); }
}
