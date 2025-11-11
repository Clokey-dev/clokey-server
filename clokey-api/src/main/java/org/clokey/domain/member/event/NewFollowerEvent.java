package org.clokey.domain.member.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NewFollowerEvent {
    private final Long followFromId;
    private final Long followToId;
}
