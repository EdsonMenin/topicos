package com.example.topicos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class PagesController {
	
	@GetMapping("/home")
	public ModelAndView home()
	{
		return new ModelAndView("home");
	}
	
	@GetMapping("/report")
	public ModelAndView report()
	{
		return new ModelAndView("report");
	}
	
	@GetMapping("/prob")
	public ModelAndView prob()
	{
		return new ModelAndView("prob");
	}
}
