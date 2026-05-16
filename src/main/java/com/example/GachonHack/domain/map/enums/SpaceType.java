package com.example.GachonHack.domain.map.enums;

import java.util.Set;

public final class SpaceType {
    /** 층 복도(맵 이동·하위 강의실/스터디룸의 parent) */
    public static final String CORRIDOR = "CORRIDOR";
    public static final String LECTURE_ROOM = "LECTURE_ROOM";
    public static final String STUDY_ROOM = "STUDY_ROOM";

    public static final Set<String> ENTERABLE_ROOM_TYPES = Set.of(LECTURE_ROOM, STUDY_ROOM);

    private SpaceType() {
    }
}
