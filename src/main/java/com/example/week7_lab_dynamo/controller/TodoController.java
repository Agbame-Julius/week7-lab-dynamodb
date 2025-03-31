package com.example.week7_lab_dynamo.controller;

import com.example.week7_lab_dynamo.model.TodoItem;
import com.example.week7_lab_dynamo.model.TodoStatus;
import com.example.week7_lab_dynamo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
public class TodoController {
    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public String getAllTodos(Model model) {
        model.addAttribute("todos", todoService.findAll());
        model.addAttribute("newTodo", new TodoItem());
        model.addAttribute("statuses", todoService.getAllStatuses());
        return "index";
    }

    @PostMapping
    public String createTodo(@ModelAttribute TodoItem todoItem) {
        todoService.create(todoItem.getTitle());
        return "redirect:/";
    }

    @GetMapping("/updateStatus/{id}/{status}")
    public String updateTodoStatus(@PathVariable String id, @PathVariable TodoStatus status) {
        todoService.updateStatus(id, status);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        todoService.findById(id).ifPresent(todoItem -> {
            model.addAttribute("todo", todoItem);
            model.addAttribute("statuses", todoService.getAllStatuses());
        });
        return "edit";
    }

    @PostMapping("/update/{id}")
    public String updateTodo(@PathVariable String id, @ModelAttribute TodoItem todoItem) {
        todoService.update(id, todoItem.getTitle(), todoItem.getStatus());
        return "redirect:/";
    }

    @GetMapping("/delete/{id}")
    public String deleteTodo(@PathVariable String id) {
        todoService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/filter/{status}")
    public String filterByStatus(@PathVariable TodoStatus status, Model model) {
        model.addAttribute("todos", todoService.findByStatus(status));
        model.addAttribute("newTodo", new TodoItem());
        model.addAttribute("statuses", todoService.getAllStatuses());
        model.addAttribute("currentFilter", status);
        return "index";
    }
}