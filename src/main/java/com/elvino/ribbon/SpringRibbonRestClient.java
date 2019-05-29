package com.elvino.ribbon;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class SpringRibbonRestClient {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SpringRibbonInterface springRibbon;
	
	@GetMapping("/client")
	public String client() {
		String hasil = restTemplate.getForObject("http://SPRINGRIBBONSERVER/fetch", String.class);
		return hasil;
	}
	
	@GetMapping("/feign/client")
	public String feignClient() {
		return springRibbon.fetch();
	}
	
	@GetMapping("/feign/feignGet")
	public Map<String, Object> feignGet() {
		return springRibbon.feignGet(40L, "Constantine Davin Ethan");
	}
	
	@GetMapping("/feign/feignPost")
	public Map<String, Object> feignPost() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("first", "pertama");
		param.put("second","kedua");
		return springRibbon.feignPost(45L, param);
	}
}
