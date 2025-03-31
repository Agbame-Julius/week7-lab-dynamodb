package com.example.week7_lab_dynamo.service;

import com.example.week7_lab_dynamo.model.TodoStatus;
import com.example.week7_lab_dynamo.model.TodoItem;
import com.example.week7_lab_dynamo.Repository.TodoDynamoDbRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private final TodoDynamoDbRepository todoRepository;

    @Autowired
    public TodoService(TodoDynamoDbRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<TodoItem> findAll() {
        return todoRepository.findAll();
    }

    public List<TodoItem> findByStatus(TodoStatus status) {
        return todoRepository.findByStatus(status);
    }

    public Optional<TodoItem> findById(String id) {
        return todoRepository.findById(id);
    }

    public TodoItem save(TodoItem todoItem) {
        todoItem.setUpdatedAt(LocalDateTime.now());
        return todoRepository.save(todoItem);
    }

    public TodoItem create(String title) {
        return todoRepository.save(new TodoItem(title));
    }

    public void delete(String id) {
        todoRepository.deleteById(id);
    }

    public void updateStatus(String id, TodoStatus status) {
        todoRepository.findById(id).ifPresent(todoItem -> {
            todoItem.setStatus(status);
            todoItem.setUpdatedAt(LocalDateTime.now());
            todoRepository.save(todoItem);
        });
    }

    public TodoItem update(String id, String title, TodoStatus status) {
        TodoItem updatedItem = null;
        Optional<TodoItem> optionalTodoItem = todoRepository.findById(id);
        if (optionalTodoItem.isPresent()) {
            TodoItem todoItem = optionalTodoItem.get();
            todoItem.setTitle(title);
            todoItem.setStatus(status);
            todoItem.setUpdatedAt(LocalDateTime.now());
            updatedItem = todoRepository.save(todoItem);
        }
        return updatedItem;
    }

    public List<TodoStatus> getAllStatuses() {
        return Arrays.asList(TodoStatus.values());
    }
}