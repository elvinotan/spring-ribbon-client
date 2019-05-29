package com.elvino.ribbon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SpringRibbonRestClient {

	@Autowired
	private RestTemplate restTemplate;
	
	@GetMapping("/client")
	public String client() {
		String hasil = restTemplate.getForObject("http://SPRINGRIBBONSERVER/fetch", String.class);
		return hasil;
	}
}
