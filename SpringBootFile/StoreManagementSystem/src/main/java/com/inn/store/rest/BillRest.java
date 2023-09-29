package com.inn.store.rest;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inn.store.POJO.Bill;

@RequestMapping(path ="/bill")
public interface BillRest {

	
	@PostMapping(path ="/generateReport")
	public ResponseEntity<String> generateReport(@RequestBody Map<String,Object>requestMap);

	@GetMapping(path= "/getBills")
	public ResponseEntity<List<Bill>> getBills();
	
	@PostMapping(path ="/getPdf")
	public ResponseEntity<byte[]> getPdf(@RequestBody Map<String,Object>requestMap);
	
	@PostMapping(path ="/delete/{id}")
	public ResponseEntity<String> deleteBill(@PathVariable Integer id);


	
	
	  
}
