package io.axoniq.demo.bikerental.rental.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;

@SpringBootApplication
public class RentalCommandApplication {

	public static void main(String[] args) {
		  SpringApplication.run(RentalCommandApplication.class, args);
		//Arrays.stream(applicationContext.getBeanDefinitionNames()).sequential().forEach(System.out::println);

	}

	@Autowired
	public void configureSerializers(ObjectMapper objectMapper) {
		objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
	}
}
