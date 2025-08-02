package org.clokey.domain.notification.repository;

import org.clokey.notification.entity.ClokeyNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<ClokeyNotification, Long> {}
