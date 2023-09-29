package com.inn.store.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(path="/dashboard")
public interface DashboardRest {
	
	
	@GetMapping(path= "/details")
	public ResponseEntity<Map<String,Object>> getCount();

}
