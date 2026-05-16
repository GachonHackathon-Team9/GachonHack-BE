package com.example.GachonHack.domain.map.dto.res;

import java.time.LocalTime;
import java.util.List;

public class MapResponseDTO {

    public record FloorItemDTO(
            Long floorId,
            String code,
            String name,
            String mapAssetKey,
            Integer sortOrder
    ) {}

    public record FloorListResDTO(
            List<FloorItemDTO> floors
    ) {}

    public record ClassroomItemDTO(
            Long classroomId,
            String code,
            String name,
            String mapAssetKey,
            Integer sortOrder
    ) {}

    public record ClassroomListResDTO(
            Long floorId,
            String floorName,
            List<ClassroomItemDTO> classrooms
    ) {}

    public record ClassChatRoomItemDTO(
            Long chatRoomId,
            Long chatSpaceId,
            String roomName
    ) {}

    public record ClassroomDetailResDTO(
            Long classroomId,
            String classroomName,
            Long floorId,
            String floorName,
            List<ScheduleItemDTO> schedules,
            List<ClassChatRoomItemDTO> chatRooms
    ) {}

    public record TimetableResDTO(
            Long spaceId,
            String spaceName,
            List<ScheduleItemDTO> schedules
    ) {}

    public record ScheduleItemDTO(
            Long id,
            Byte dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            String courseName,
            String professor
    ) {}
}
