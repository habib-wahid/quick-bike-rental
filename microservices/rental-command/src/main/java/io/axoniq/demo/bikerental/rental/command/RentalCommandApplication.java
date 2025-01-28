package io.axoniq.demo.bikerental.rental.command;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.config.Configuration;
import org.axonframework.config.ConfigurerModule;
import org.axonframework.queryhandling.QueryBus;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.axonframework.queryhandling.SimpleQueryBus;
import org.axonframework.springboot.autoconfig.XStreamAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@SpringBootApplication(exclude = {XStreamAutoConfiguration.class})
public class RentalCommandApplication {

	public static void main(String[] args) {
		  SpringApplication.run(RentalCommandApplication.class, args);
		//Arrays.stream(applicationContext.getBeanDefinitionNames()).sequential().forEach(System.out::println);

	}

	@Autowired
	public void configureSerializers(ObjectMapper objectMapper) {
		objectMapper.registerModule(new JavaTimeModule())
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
				.activateDefaultTyping(
						objectMapper.getPolymorphicTypeValidator(),
						ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT
				);
	}


	@Bean
	@Primary
	public CommandBus commandBus(TransactionManager transactionManager) {
		SimpleCommandBus.Builder builder = SimpleCommandBus.builder();
		return builder
				.transactionManager(transactionManager)

				.build();
	}

	@Bean
	@Primary
	public QueryBus queryBus() {
		return SimpleQueryBus.builder().build();
	}

	@Bean
	public QueryUpdateEmitter queryUpdateEmitter(Configuration configuration) {
		return configuration.queryUpdateEmitter();
	}

}
