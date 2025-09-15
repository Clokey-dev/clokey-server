package org.clokey.report.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum TargetType {
    COMMENT("댓글"),
    REPLY("대댓글"),
    HISTORY("기록");

    private final String description;
}
