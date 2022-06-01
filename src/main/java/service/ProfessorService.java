package service;

import org.springframework.stereotype.Service;

import entity.Allocation;
import entity.Department;
import entity.Professor;
import repository.AllocationRepository;
import repository.ProfessorRepository;

import java.util.List;

@Service
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final DepartmentService departmentService;
    private final AllocationRepository allocationRepository;

    public ProfessorService(ProfessorRepository professorRepository, DepartmentService departmentService,
                            AllocationRepository allocationRepository) {
        super();
        this.professorRepository = professorRepository;
        this.departmentService = departmentService;
        this.allocationRepository = allocationRepository;
    }

    public List<Professor> findAll(String name) {
        if (name == null) {
            return professorRepository.findAll();
        } else {
            return professorRepository.findByNameContainingIgnoreCase(name);
        }
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id).orElse(null);
    }

    public List<Professor> findByDepartment(Long departmentId) {
        return professorRepository.findByDepartmentId(departmentId);
    }

    public Professor save(Professor professor) {
        professor.setId(null);
        return saveInternal(professor);
    }

    public Professor update(Professor professor) {
        Long id = professor.getId();
        if (id != null && professorRepository.existsById(id)) {
            return saveInternal(professor);
        } else {
            return null;
        }
    }

    public void deleteById(Long id) {
        if (id != null && professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
        }
    }

    public void deleteAll() {
        professorRepository.deleteAllInBatch();
    }

    private Professor saveInternal(Professor professor) {
        if(!isEndHourGreaterThanStartHour(professor) || hasCollision(professor)) {
        	throw new RuntimeException();
        }
        else {
        	professor = professorRepository.save(professor);

            Department department = departmentService.findById(professor.getDepartmentId());
            professor.setDepartment(department);

            List<Allocation> allocations = allocationRepository.findByProfessorId(professor.getId());
            professor.setAllocations(allocations);

            return professor;
        }
    }
    
    boolean isEndHourGreaterThanStartHour(Professor professor) {
        return professor != null && professor.getStartHour() != null && professor.getEndHour() != null
                && professor.getEndHour().compareTo(professor.getStartHour()) > 0;
    }
    
    boolean hasCollision(Professor newProfessor) {
        boolean hasCollision = false;

        List<Professor> currentProfessors = professorRepository.findByDepartmentId(newProfessor.getDepartmentId());

        for (Professor currentProfessor : currentProfessors) {
            hasCollision = hasCollision(currentProfessor, newProfessor);
            if (hasCollision) {
                break;
            }
        }

        return hasCollision;
    }
    
    private boolean hasCollision(Professor currentProfessor, Professor newProfessor) {
        return !currentProfessor.getId().equals(newProfessor.getId())
                && currentProfessor.getDayOfWeek() == newProfessor.getDayOfWeek()
                && currentProfessor.getStartHour().compareTo(newProfessor.getEndHour()) < 0
                && newProfessor.getStartHour().compareTo(currentProfessor.getEndHour()) < 0;
    }
}
