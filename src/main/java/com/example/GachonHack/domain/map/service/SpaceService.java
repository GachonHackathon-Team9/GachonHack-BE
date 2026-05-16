package com.example.GachonHack.domain.map.service;

import com.example.GachonHack.domain.community.entity.ChatRoom;
import com.example.GachonHack.domain.community.repository.ChatRoomRepository;
import com.example.GachonHack.domain.map.dto.res.MapResponseDTO;
import com.example.GachonHack.domain.map.entity.ClassroomSchedule;
import com.example.GachonHack.domain.map.entity.Space;
import com.example.GachonHack.domain.map.enums.SpaceType;
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
    private final ChatRoomRepository chatRoomRepository;

    /**
     * 층 맵 목록 — DB type {@code CORRIDOR} (예: AI관 5층 복도).
     * API 경로는 프론트 호환을 위해 /floors 를 유지합니다.
     */
    @Transactional(readOnly = true)
    public MapResponseDTO.FloorListResDTO getFloors() {
        List<MapResponseDTO.FloorItemDTO> floors = spaceRepository.findByTypeOrderBySortOrderAsc(SpaceType.CORRIDOR)
                .stream()
                .map(this::toFloorItem)
                .toList();
        return new MapResponseDTO.FloorListResDTO(floors);
    }

    /**
     * 복도 하위 강의실·스터디룸 — {@code LECTURE_ROOM}, {@code STUDY_ROOM}.
     */
    @Transactional(readOnly = true)
    public MapResponseDTO.ClassroomListResDTO getClassroomsByFloor(Long corridorId) {
        Space corridor = findCorridor(corridorId);
        List<MapResponseDTO.ClassroomItemDTO> classrooms = spaceRepository
                .findByParentAndTypeInOrderBySortOrderAsc(corridor, SpaceType.ENTERABLE_ROOM_TYPES)
                .stream()
                .map(this::toClassroomItem)
                .toList();
        return new MapResponseDTO.ClassroomListResDTO(corridor.getId(), corridor.getName(), classrooms);
    }

    @Transactional(readOnly = true)
    public MapResponseDTO.ClassroomDetailResDTO getClassroomDetail(Long roomId) {
        Space room = findEnterableRoom(roomId);
        Space corridor = room.getParent();
        if (corridor == null || !SpaceType.CORRIDOR.equals(corridor.getType())) {
            throw new MapException(MapErrorCode.INVALID_SPACE_TYPE);
        }

        List<MapResponseDTO.ScheduleItemDTO> schedules = classroomScheduleRepository
                .findBySpaceOrderByDayOfWeekAscStartTimeAsc(room)
                .stream()
                .map(this::toScheduleItem)
                .toList();

        List<MapResponseDTO.ClassChatRoomItemDTO> chatRooms = chatRoomRepository
                .findActiveSpaceChatRoomsBySpaceId(room.getId())
                .stream()
                .map(this::toClassChatRoomItem)
                .toList();

        return new MapResponseDTO.ClassroomDetailResDTO(
                room.getId(),
                room.getName(),
                corridor.getId(),
                corridor.getName(),
                schedules,
                chatRooms
        );
    }

    @Transactional(readOnly = true)
    public MapResponseDTO.TimetableResDTO getTimetable(Long spaceId) {
        Space room = findEnterableRoom(spaceId);
        List<MapResponseDTO.ScheduleItemDTO> schedules = classroomScheduleRepository
                .findBySpaceOrderByDayOfWeekAscStartTimeAsc(room)
                .stream()
                .map(this::toScheduleItem)
                .toList();
        return new MapResponseDTO.TimetableResDTO(room.getId(), room.getName(), schedules);
    }

    private Space findCorridor(Long corridorId) {
        Space corridor = spaceRepository.findById(corridorId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));
        if (!SpaceType.CORRIDOR.equals(corridor.getType())) {
            throw new MapException(MapErrorCode.INVALID_SPACE_TYPE);
        }
        return corridor;
    }

    private Space findEnterableRoom(Long roomId) {
        Space room = spaceRepository.findById(roomId)
                .orElseThrow(() -> new MapException(MapErrorCode.SPACE_NOT_FOUND));
        if (!SpaceType.ENTERABLE_ROOM_TYPES.contains(room.getType())) {
            throw new MapException(MapErrorCode.INVALID_SPACE_TYPE);
        }
        return room;
    }

    private MapResponseDTO.FloorItemDTO toFloorItem(Space corridor) {
        return new MapResponseDTO.FloorItemDTO(
                corridor.getId(),
                corridor.getCode(),
                corridor.getName(),
                corridor.getMapAssetKey(),
                corridor.getSortOrder()
        );
    }

    private MapResponseDTO.ClassroomItemDTO toClassroomItem(Space room) {
        return new MapResponseDTO.ClassroomItemDTO(
                room.getId(),
                room.getCode(),
                room.getName(),
                room.getMapAssetKey(),
                room.getSortOrder()
        );
    }

    private MapResponseDTO.ClassChatRoomItemDTO toClassChatRoomItem(ChatRoom room) {
        Space chatSpace = room.getSpace();
        return new MapResponseDTO.ClassChatRoomItemDTO(
                room.getId(),
                chatSpace.getId(),
                chatSpace.getName()
        );
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
