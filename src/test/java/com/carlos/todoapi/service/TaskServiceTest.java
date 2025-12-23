package com.carlos.todoapi.service;

import com.carlos.todoapi.dto.request.CreateTaskRequest;
import com.carlos.todoapi.dto.response.TaskResponse;
import com.carlos.todoapi.entity.Task;
import com.carlos.todoapi.entity.TaskStatus;
import com.carlos.todoapi.entity.User;
import com.carlos.todoapi.exception.TaskNotFoundException;
import com.carlos.todoapi.exception.UnauthorizedAccessException;
import com.carlos.todoapi.exception.UserNotFoundException;
import com.carlos.todoapi.mapper.TaskMapper;
import com.carlos.todoapi.repository.TaskRepository;
import com.carlos.todoapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private User mockUser;
    private Task mockTask;
    private TaskResponse mockTaskResponse;
    private CreateTaskRequest createRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockTask = new Task();
        mockTask.setId(1L);
        mockTask.setTitle("Test Task");
        mockTask.setStatus(TaskStatus.PENDING);
        mockTask.setUser(mockUser);

        mockTaskResponse = new TaskResponse(
                1L, "Test Task", null, TaskStatus.PENDING, null, null, 1L, "testuser", null, null
        );

        createRequest = new CreateTaskRequest(
                "Test Task", null, null, null, null
        );
    }


    @Test
    @DisplayName("Should create task successfully")
    void testCreateTask_Success() {
        //Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(taskMapper.toEntity(any(CreateTaskRequest.class), any(User.class))).thenReturn(mockTask);
        when(taskRepository.save(any(Task.class))).thenReturn(mockTask);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(mockTaskResponse);

        //Act
        TaskResponse response = taskService.createTask(createRequest, "testuser");

        //Assert
        assertNotNull(response);
        assertEquals("Test Task", response.title());
        assertEquals(TaskStatus.PENDING, response.status());

        verify(userRepository).findByUsername("testuser");
        verify(taskRepository).save(any(Task.class));

    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void testCreateTask_UserNotFound() {
        //Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(UserNotFoundException.class, () -> {
            taskService.createTask(createRequest, "nonexistent");
        });


        verify(userRepository).findByUsername("nonexistent");
        verify(taskRepository, never()).save(any(Task.class));
    }


    @Test
    @DisplayName("Should get tasks by user with pagination")
    void testGetTasksByUser_Success() {
        //Arrange
        Page<Task> taskPage = new PageImpl<>(Arrays.asList(mockTask));
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(mockUser));
        when(taskRepository.findByUserId(anyLong(), any(Pageable.class))).thenReturn(taskPage);
        when(taskMapper.toResponse(any(Task.class))).thenReturn(mockTaskResponse);

        //Act
        Page<TaskResponse> result = taskService.getTasksByUser("testuser", pageable);

        //Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Task", result.getContent().getFirst().title());

        verify(userRepository).findByUsername("testuser");
        verify(taskRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("Should throw exception when acessing task from another user")
    void testGetTaskById_UnauthorizedAccess() {
        //Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(mockTask));

        //Act and Assert
        assertThrows(UnauthorizedAccessException.class, () -> {
            taskService.getTaskById(1L, "anotheruser");
        });

        verify(taskRepository).findById(1L);
    }

    @Test
    @DisplayName("Should delete task successfully")
    void testDeleteTask_Success() {
        //Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(mockTask));
        doNothing().when(taskRepository).delete(any(Task.class));

        //Act
        taskService.deleteTask(1L, "testuser");

        //Assert
        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(mockTask);

    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent task")
    void testDeleteTask_NotFound() {
        //Arrange
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(999L, "testuser");
        });

        verify(taskRepository).findById(999L);
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
