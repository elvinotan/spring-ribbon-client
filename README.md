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
