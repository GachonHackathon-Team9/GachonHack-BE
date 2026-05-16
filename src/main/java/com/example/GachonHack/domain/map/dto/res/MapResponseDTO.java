package com.example.GachonHack.domain.map.dto.res;

import java.time.LocalTime;
import java.util.List;

public class MapResponseDTO {

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
