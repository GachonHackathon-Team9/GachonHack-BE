package com.example.GachonHack.domain.map.repository;

import com.example.GachonHack.domain.map.entity.ClassroomSchedule;
import com.example.GachonHack.domain.map.entity.Space;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomScheduleRepository extends JpaRepository<ClassroomSchedule, Long> {

    List<ClassroomSchedule> findBySpaceOrderByDayOfWeekAscStartTimeAsc(Space space);
}
