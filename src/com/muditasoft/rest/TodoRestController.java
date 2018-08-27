package com.muditasoft.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.muditasoft.model.Todo;
import com.muditasoft.service.TodoService;

@RestController
public class TodoRestController {
	@Autowired
	private TodoService service;

	@RequestMapping(value = "/todos")
	public List<Todo> retrieveAllTodos() {
		return service.retrieveTodos("MuditaSoftAcademy");
	}

	@RequestMapping(value = "/todos/{id}")
	public Todo retrieveTodo(@PathVariable int id) {
		return service.retrieveTodo(id);
	}

}