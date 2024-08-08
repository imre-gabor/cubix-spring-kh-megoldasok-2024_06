package hu.webuni.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import hu.webuni.tokenlib.JwtAuthFilter;

@SpringBootApplication (scanBasePackageClasses = {JwtAuthFilter.class, GatewayApplication.class})
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
