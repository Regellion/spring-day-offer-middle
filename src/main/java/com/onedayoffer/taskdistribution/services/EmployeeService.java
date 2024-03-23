package com.onedayoffer.taskdistribution.services;

import com.onedayoffer.taskdistribution.DTO.EmployeeDTO;
import com.onedayoffer.taskdistribution.DTO.TaskDTO;
import com.onedayoffer.taskdistribution.DTO.TaskStatus;
import com.onedayoffer.taskdistribution.repositories.EmployeeRepository;
import com.onedayoffer.taskdistribution.repositories.TaskRepository;
import com.onedayoffer.taskdistribution.repositories.entities.Employee;
import com.onedayoffer.taskdistribution.repositories.entities.Task;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    public List<EmployeeDTO> getEmployees(@Nullable String sortDirection) {
        if(sortDirection == null || sortDirection.isEmpty()) {
            return employeeRepository.findAll()
                    .stream()
                    .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                    .collect(Collectors.toList());
        }
        if(!sortDirection.equalsIgnoreCase(Sort.Direction.ASC.toString()) && !sortDirection.equalsIgnoreCase(Sort.Direction.DESC.toString())){
            throw new IllegalArgumentException("Неверный параметр сортировки: " + sortDirection);
        }
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection), "fio");
        return employeeRepository.findAllAndSort(sort)
                .stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public EmployeeDTO getOneEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Работник с id " + id + " не найден в базе данных"));    //todo в exceptHandler
        return modelMapper.map(employee, EmployeeDTO.class);
    }

    public List<TaskDTO> getTasksByEmployeeId(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Работник с id " + id + " не найден в базе данных"));
        return employee.getTasks().stream().map(task -> modelMapper.map(task, TaskDTO.class)).toList();
    }

    @Transactional
    public void changeTaskStatus(Integer taskId, TaskStatus status) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Задача с id " + taskId + " не найден в базе данных"));
        if (task.getStatus().equals(status)) {
            log.warn("Статус задачи с id {} уже установлен как {}", taskId, status);
            return;
        }
        task.setStatus(status);
        taskRepository.save(task);
    }

    @Transactional
    public void postNewTask(Integer employeeId, TaskDTO newTask) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("Работник с id " + employeeId + " не найден в базе данных"));
        Task task = modelMapper.map(newTask, Task.class);
        employee.addTask(task);
        employeeRepository.save(employee);
    }
}
