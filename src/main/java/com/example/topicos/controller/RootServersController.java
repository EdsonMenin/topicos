package com.example.topicos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.topicos.service.RootServersService;

@RestController
@RequestMapping("/rootServers")
public class RootServersController {
	
	@Autowired
	RootServersService rootServersService;
	
	@GetMapping("/dns/{rootServer}/{dateInitial}/{dateFinal}")
	public ResponseEntity<Object> dns( 	@PathVariable String rootServer,
										@PathVariable String dateInitial,
										@PathVariable String dateFinal )
	{
		try {
			return ResponseEntity.ok(
					rootServersService.calculateInfos(rootServer, dateInitial, dateFinal) );
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
