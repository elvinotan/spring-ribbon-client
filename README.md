# Spring-Ribbon-Client
Spring Ribbon Client ini hanya merupaakn microserver, yang akan mensimulasikan ribbon sebagai client loadbalancer. Pada aplikasi ini mencoba untuk mengambil data dari Spring Ribbon Server (terdiri dari beberapa instance yang jalan di berbeda port). Dgn harapan setiap fetching data akan di ambil dari instance yang berbeda

# Dependencies
Karena kita mau agar micro service ini juga konfigurasinya di handle oleh config server maka, dependencies untuk config client juga masuk, tapi apabila tidak, maka tidak perlu di masuk

Eureka Discovery</br>
Web</br>
Config Client</br>

# How to
1. Tambahkan @EnableDiscoveryClient pada SpringBootApplicationClass, dengan konfigurasi ini menandakan service ini ingin mendaftar dirinya pakan EurekaServer. Sebenarnya ada @EnableEurekaClient tapi kita tidak gunakan ini krn tidak semua server menggunakan eureka dan untuk amannya kita gunakan @EnableDiscoveryClient
2. Untuk mekanisme LoadBalancer, akan di terapakan pada Object RestTemplate, sebagai object yang akan mengatur dengan mekanisme Round Robin
```
@SpringBootApplication
@EnableDiscoveryClient
public class SpringRibbonClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRibbonClientApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
```
3. Buat RestController sebagai entrypoint yang memanggil method yang ada di SpringRibbonServer
```
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
```
4. Lakukan testing dengan menggunakan postman
# Note
Untuk testing ini bisa berjalan, maka pada SpringRibbonServer harus dijalankan lebih dari satu instance dengan port yang berbeda, laku kita lakukan command call dgn menggunkan RestTemplate dgn format http://{spring-ribbon-server}/{commmand}. Bila anda perhatikan pada command tersebut tidak di sertakan port

# Add Feign Support
# Dependencies
Eureka Discovery</br>
Web</br>
Config Client</br>
Feign</br>

# How to
1. Tambahkan @EnableFeignClients pada SpringApplication Class
```
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SpringRibbonClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRibbonClientApplication.class, args);
	}

	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
}
```
2. Buat Interface sebagai shorthand untuk call Other microservices
```
@FeignClient("SpringRibbonServer")
public interface SpringRibbonInterface {

	@GetMapping("/fetch")
	public String fetch();
	
	@GetMapping("/feignGet/{nomor}")
	public Map<String, Object> feignGet(@PathVariable("nomor") Long nomor, @RequestParam("nama") String nama);
	
	@PostMapping("/feignPost/{nomor}")
	public Map<String, Object> feignPost(@PathVariable("nomor") Long nomor, @RequestBody Map<String, Object> param);
}
```
SpringRibbonServer merupakan nama dari service yang akan kita panggil</br>
3. Panggil method yang sudah di jalabarkan di interface
```
	@Autowired
	private SpringRibbonInterface springRibbon;
	
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
```
# Note
Keuntungan menggunakan feign adalah, feign sudah men-support Ribbon (Load Balancer) dan mempermudah unit testing

# Add Hytrix Support
# Dependencies
Eureka Discovery</br>
Web</br>
Config Client</br>
Feign</br>
Hystrix</br>

# How to
1. Tambahkan @EnableHystrix pada SpringApplication Class
2. Tambahkan anotation pada method yang mau di jaga apabila terjadi error
```
@HystrixCommand(groupKey="fallback", commandKey="feignGet", fallbackMethod="feignGetCallback")
@GetMapping("/feign/feignGet")
public Map<String, Object> feignGet() {
	return springRibbon.feignGet(40L, "Constantine Davin Ethan");
}
```
untuk groupKey dan commandKey saat ini masih blm tau kegunaanya, tp spring menyediakn hystrix dashboard, sepertinya ini hanya sebagai log saja, untuk fallbackMethod, adalah method yang akan di panggil apabila terjadi error
```
public Map<String, Object> feignGetCallback() {
	Map<String, Object> map = new HashMap<String, Object>();
	map.put("fallback", "true");
	map.put("error", "Hytrix error accured");
	return map;
}
```
# Note
Sebenarnya option dari hystrix masih banyak lagi, sperti apabila dalam 5 menit terjadi 20 error maka circuit break akan open dan fallbackMethod akan di panggil, ada juga circuit yang open akan dipertahankan selam 2 menit setelah itu akan di coba close kembali

