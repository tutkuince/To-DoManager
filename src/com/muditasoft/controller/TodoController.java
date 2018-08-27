package com.muditasoft.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.muditasoft.exception.ExceptionController;
import com.muditasoft.model.Todo;
import com.muditasoft.service.TodoService;

@Controller
@SessionAttributes("name")
public class TodoController {

	private Log logger = LogFactory.getLog(ExceptionController.class);

	@Autowired
	private TodoService service;

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	}

	@GetMapping(value = "/list-todos")
	public String listTodos(Model model) {
		model.addAttribute("todos", service.retrieveTodos(retrieveLoggedinUserName()));
		return "list-todos";
	}

	private String retrieveLoggedinUserName() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails)
			return ((UserDetails) principal).getUsername();

		return principal.toString();
	}

	@GetMapping(value = "/add-todo")
	public String showTodoPage(Model model) {

		model.addAttribute("todo", new Todo(0, retrieveLoggedinUserName(), "", new Date(), false));
		return "todo";

	}

	@PostMapping(value = "/add-todo")
	public String addTodo(ModelMap model, @Validated Todo todo, BindingResult result) {
		if (result.hasErrors()) {
			return "todo";
		}
		service.addTodo(retrieveLoggedinUserName(), todo.getDesc(), new Date(), false);
		model.clear();
		return "redirect:list-todos";
	}

	@GetMapping(value = "/update-todo")
	public String updateTodo(Model model, @RequestParam int id) {
		Todo todo = service.retrieveTodo(id);
		model.addAttribute("todo", todo);
		return "todo";
	}

	@PostMapping(value = "/update-todo")
	public String updateTodo(Model model, @Validated Todo todo, BindingResult result) {
		// Update to do
		if (result.hasErrors()) {
			return "todo";
		}

		todo.setUser(retrieveLoggedinUserName());

		service.updateTodo(todo);

		return "redirect:list-todos";
	}

	@GetMapping(value = "/delete-todo")
	public String deleteTodo(ModelMap model, @RequestParam int id) {
		service.deleteTodo(id);
		model.clear();
		return "redirect:list-todos";
	}

	@ExceptionHandler(value = Exception.class)
	public String handleException(HttpServletRequest request, Exception ex) {
		logger.error("Request " + request.getRequestURL() + " Threw an Exception", ex);
		return "error-specific";
	}

}
