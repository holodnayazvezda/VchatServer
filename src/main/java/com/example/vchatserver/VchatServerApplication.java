package com.example.vchatserver;

import com.example.vchatserver.gateway.SocketIOConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class VchatServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(VchatServerApplication.class, args);
	}
}
