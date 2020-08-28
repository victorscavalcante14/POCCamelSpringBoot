package br.com.poc.camel;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan("br.com.poc.camel")
public class POCCamelApplication {

	public static void main(String[] args) {
		SpringApplication.run(POCCamelApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<CamelHttpTransportServlet> camelServletRegistrationBean() {
		ServletRegistrationBean<CamelHttpTransportServlet> registration = new ServletRegistrationBean<CamelHttpTransportServlet>(new CamelHttpTransportServlet(), "/*");
		registration.setName("CamelServlet");
		return registration;
	}
}