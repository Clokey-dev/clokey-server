package org.clokey.domain.notification.service;

import org.clokey.domain.notification.dto.response.NewFollowerNotificationResponse;

public interface CodiveNotificationService {

    NewFollowerNotificationResponse sendNewFollowerNotification(Long followFromId, Long followToId);
}
