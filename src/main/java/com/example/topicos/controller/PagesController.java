package com.example.topicos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/")
public class PagesController {
	
	@GetMapping("/report")
	public ModelAndView report()
	{
		return new ModelAndView("report");
	}
}
