package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import entity.Course;
import entity.Department;
import entity.Professor;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	List<Course> findByNameContainingIgnoreCase(String name);
}
