package br.com.controlededespesas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ControlededespesasApplication {

	public static void main(String[] args) {
//		System.out.println(new BCryptPasswordEncoder().encode("123456"));
		SpringApplication.run(ControlededespesasApplication.class, args);
	}

}
