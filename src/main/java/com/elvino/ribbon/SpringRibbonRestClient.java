package com.elvino.ribbon;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
@RefreshScope
public class SpringRibbonRestClient {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private SpringRibbonInterface springRibbon;
	
	@GetMapping("/client")
	public String client() {
		String hasil = restTemplate.getForObject("http://SPRINGZULL/springribbonserver/fetch", String.class);
		//String hasil = restTemplate.getForObject("http://SPRINGRIBBONSERVER/fetch", String.class);
		return hasil;
	}
	
	@GetMapping("/feign/client")
	public String feignClient() {
		return springRibbon.fetch();
	}
	
	@HystrixCommand(groupKey="fallback", commandKey="feignGet", fallbackMethod="feignGetCallback")
	@GetMapping("/feign/feignGet")
	public Map<String, Object> feignGet() {
		return springRibbon.feignGet(40L, "Constantine Davin Ethan");
	}
	
	@PostMapping("/feign/feignPost")
	public Map<String, Object> feignPost() {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("first", "pertama");
		param.put("second","kedua");
		return springRibbon.feignPost(45L, param);
	}
	
	public Map<String, Object> feignGetCallback() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fallback", "true");
		map.put("error", "Hytrix error accured");
		return map;
	}
}
