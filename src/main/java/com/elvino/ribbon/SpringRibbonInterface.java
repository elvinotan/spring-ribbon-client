package com.elvino.ribbon;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("SpringZull")
//@FeignClient("SpringRibbonServer")
public interface SpringRibbonInterface {

	@GetMapping("/springribbonserver/fetch")
	//@GetMapping("/fetch")
	public String fetch();
	
	@GetMapping("/springribbonserver/feignGet/{nomor}")
	//@GetMapping("/feignGet/{nomor}")
	public Map<String, Object> feignGet(@PathVariable("nomor") Long nomor, @RequestParam("nama") String nama);
	
	@PostMapping("/springribbonserver/feignPost/{nomor}")
	//@PostMapping("/feignPost/{nomor}")
	public Map<String, Object> feignPost(@PathVariable("nomor") Long nomor, @RequestBody Map<String, Object> param);
}