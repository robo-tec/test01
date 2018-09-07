package com.robodev.camel.test01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class Test01Application {

	@Autowired
	private Environment env;

	public static void main(String[] args) {
		SpringApplication.run(Test01Application.class, args);
	}

	@Component
	class RestApi extends RouteBuilder {

		@Override
		public void configure() {
			restConfiguration()
					.contextPath("/camel-rest-jpa").apiContextPath("/api-doc")
					.apiProperty("api.title", "Camel REST API")
					.apiProperty("api.version", "1.0")
					.apiProperty("cors", "true")
					.apiContextRouteId("doc-api")
					.port(env.getProperty("server.port", "8080"))
					.bindingMode(RestBindingMode.json);

			rest("/books").description("Books REST service")
					.get("/").description("The list of all the books")
					.route().routeId("books-api")
					.bean(Database.class, "findBooks")
					.endRest()
					.get("order/{id}").description("Details of an order by id")
					.route().routeId("order-api")
					.bean(Database.class, "findOrder(${header.id})");
		}
	}

	@Component
	class Backend extends RouteBuilder {

		@Override
		public void configure() {
			// A first route generates some orders and queue them in DB
			from("timer:new-order?delay=1s&period={{example.generateOrderPeriod:2s}}")
					.routeId("generate-order")
					.bean("orderService", "generateOrder")
					.to("jpa:org.apache.camel.example.spring.boot.rest.jpa.Order")
					.log("Inserted new order ${body.id}");

			// A second route polls the DB for new orders and processes them
			from("jpa:org.apache.camel.example.spring.boot.rest.jpa.Order"
					+ "?consumer.namedQuery=new-orders"
					+ "&consumer.delay={{example.processOrderPeriod:5s}}"
					+ "&consumeDelete=false")
					.routeId("process-order")
					.log("Processed order #id ${body.id} with ${body.amount} copies of the «${body.book.description}» book");
		}
	}
}
