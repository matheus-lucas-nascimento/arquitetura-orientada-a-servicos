package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.Allocation;
import entity.Course;
import entity.Department;
import entity.Professor;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    List<Allocation> findByProfessorId(Long professorId);

    List<Allocation> findByCourseId(Long courseId);
}
