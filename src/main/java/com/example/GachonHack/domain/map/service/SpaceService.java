package com.example.GachonHack.domain.map.service;

import com.example.GachonHack.domain.map.dto.res.MapResponseDTO;
import com.example.GachonHack.domain.map.entity.ClassroomSchedule;
import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.map.exception.MapException;
import com.example.GachonHack.domain.map.exception.code.MapErrorCode;
import com.example.GachonHack.domain.map.repository.ClassroomScheduleRepository;
import com.example.GachonHack.domain.map.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final ClassroomScheduleRepository classroomScheduleRepository;

    @Transactional(readOnly = true)
    public MapResponseDTO.TimetableResDTO getTimetable(Long spaceId) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));

        List<MapResponseDTO.ScheduleItemDTO> schedules = classroomScheduleRepository
                .findBySpaceOrderByDayOfWeekAscStartTimeAsc(space)
                .stream()
                .map(this::toScheduleItem)
                .toList();

        return new MapResponseDTO.TimetableResDTO(space.getId(), space.getName(), schedules);
    }

    private MapResponseDTO.ScheduleItemDTO toScheduleItem(ClassroomSchedule schedule) {
        return new MapResponseDTO.ScheduleItemDTO(
                schedule.getId(),
                schedule.getDayOfWeek(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getCourseName(),
                schedule.getProfessor()
        );
    }
}
