package service;

import org.springframework.stereotype.Service;

import entity.Department;
import entity.Professor;
import repository.DepartmentRepository;
import repository.ProfessorRepository;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ProfessorRepository professorRepository;

    public DepartmentService(DepartmentRepository departmentRepository, ProfessorRepository professorRepository) {
        super();
        this.departmentRepository = departmentRepository;
        this.professorRepository = professorRepository;
    }

    public List<Department> findAll(String name) {
        if (name == null) {
            return departmentRepository.findAll();
        } else {
            return departmentRepository.findByNameContainingIgnoreCase(name);
        }
    }

    public Department findById(Long id) {
        return departmentRepository.findById(id).orElse(null);
    }

    public Department save(Department department) {
        department.setId(null);
        return saveInternal(department);
    }

    public Department update(Department department) {
        Long id = department.getId();
        if (id != null && departmentRepository.existsById(id)) {
            return saveInternal(department);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        if (id != null && departmentRepository.existsById(id)) {
            departmentRepository.deleteById(id);
        }
    }

    public void deleteAll() {
        departmentRepository.deleteAllInBatch();
    }

    private Department saveInternal(Department department) {
        if(!isEndHourGreaterThanStartHour(department) || hasCollision(department)) {
        	throw new RuntimeException();    	
        }
        else {
        	department = departmentRepository.save(department);

            List<Professor> professors = professorRepository.findByDepartmentId(department.getId());
            department.setProfessors(professors);

            return department;
        }
    }
    
    boolean isEndHourGreaterThanStartHour(Department department) {
        return department != null && department.getStartHour() != null && department.getEndHour() != null
                && department.getEndHour().compareTo(department.getStartHour()) > 0;
    }
    
    boolean hasCollision(Department newDepartment) {
        boolean hasCollision = false;

        List<Department> currentDepartments = departmentRepository.findByNameContainingIgnoreCase(newDepartment.getName());

        for (Department currentDepartment : currentDepartments) {
            hasCollision = hasCollision(currentDepartment, newDepartment);
            if (hasCollision) {
                break;
            }
        }

        return hasCollision;
    }
    
    private boolean hasCollision(Department currentDepartment, Department newDepartment) {
        return !currentDepartment.getId().equals(newDepartment.getId())
                && currentDepartment.getDayOfWeek() == newDepartment.getDayOfWeek()
                && currentDepartment.getStartHour().compareTo(newDepartment.getEndHour()) < 0
                && newDepartment.getStartHour().compareTo(currentDepartment.getEndHour()) < 0;
    }
}
