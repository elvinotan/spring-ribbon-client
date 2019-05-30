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
Feign adalah suatu shorthand, untuk memudahkan kita memanggil rest endpoint dengan membuat inferce, layaknya spring Data

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
Hytrix adalah framework yang bertujuan untuk menghandle error yang terjadi pada aplikasi, logika Hystrix sama dengan logika saklar di rumah

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
Sebenarnya option dari hystrix masih banyak lagi, sperti apabila dalam 5 menit terjadi 20 error maka circuit break akan open dan fallbackMethod akan di panggil, ada juga circuit yang open akan dipertahankan selam 2 menit setelah itu akan di coba close kembali</br>
Untuk memonitor fallback bisa menggunakan Hytrix Dashboard/ Turbine (Belum di explor)

# Add Spring Bus Support
Spring Bus memungkinkan bila ada perubahan di file configurasi langsung di sebarkan perubahan tersebut kepada client yang mendengarkan dengan @RefreshScope. Cara kerja dari Spring Bus ini adalah, saat file configursi telah di ubah, lalu kita lakukan server config : post refresh, lalu pesan tersebut di teruskan melalui amqp (Ex:RabbitMQ), dan semua client yang memiliki dependenciy spring bus dan acuator akan otomatis menerima perubahan tersebut

# Dependencies
Actuator</br>
Spring Bus</br>

# How to
Pada tahap ini, perubahan yang dilakukan tidak hanya pada satu project tapi dari beberpa project, terutama pada Spring Config Server, krn trigger perubahan akan dilakukan pada Spring Config Server (Yg memberi perintah untuk reload change)
1. Pada Spring Config Server, tambahan 2 dependencies. Sama halnya dengan client yang akan menerima perubahan tersebut
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.cloud</groupId>
	<artifactId>spring-cloud-starter-bus-amqp</artifactId>
</dependency>
```
2. Spring Bus ini memerukan teknologi lain untuk berfungsi yaitu amqp framework, maka dari itu install RabbitMQ
3. Pada Saat Server Config di hidupakan, Perhatikan dia akan mencoba untuk berkomunikasi dengan RabbitMQ Server
4. Untuk mentrigger perubahan configurasi maka kita harus jalankan perintah POST http://{host}:{port}/actuator/bus-refresh pada Server Config
5. Perubahan akan di terima oleh Client, jgn lupa untuk menambahkan @RefreshScope pada client

# Note
Untuk menjalankan bus-refresh di butuhkan hak access POST, maka kita harus menambahkan hak tersebut 
```
management:
  endpoints:
    web:
      exposure:
        include: bus-refresh,refresh
```
kadang pada tutorial di youtube atau yang lain, mereka menyarankan untuk memasang management.security.enabled=false. Tapi property ini sudah depricated
