package com.coderuler.productservice;

import com.coderuler.productservice.model.Product;
import com.coderuler.productservice.respository.ProductRepository;
import com.coderuler.productservice.dto.ProductRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Assertions;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.0.10");

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@BeforeEach
	void setUp() {
		// Clear the database before each test
		productRepository.deleteAll();
	}

	@Test
	void shouldCreateProducts() throws Exception {
		ProductRequest productRequest = getProductRequest();
		String productRequestString = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone 13")
				.description("iPhone 13")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

	private ProductRequest getProductRequest(String name, String desc, BigDecimal price) {
		return ProductRequest.builder()
				.name(name)
				.description(desc)
				.price(price)
				.build();
	}

	@Test
	void shouldGetProduct() throws Exception {
		// create an instance of the product and save it to the repository
		// We first create the ProductRequest object and use it to create the Product -> to simulate the end-end flow
		ProductRequest productRequest = getProductRequest("Samsung TV", "Samsung TV", BigDecimal.valueOf(43000));
		Product product = new Product();
		product.setName(productRequest.getName());
		product.setDescription(productRequest.getDescription());
		product.setPrice(productRequest.getPrice());
		productRepository.save(product);

		// simulate the GET request via mockmvc to fetch that product's details: name, description, price
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name").value(productRequest.getName()))
				.andExpect(jsonPath("$[0].description").value(productRequest.getDescription()))
				.andExpect(jsonPath("$[0].price").value(productRequest.getPrice().intValue()));

	}

	@Test
	void shouldGetAllProducts() throws Exception {
		// First, create a couple of products to ensure there is data to fetch
		ProductRequest productRequest1 = getProductRequest("iPhone 13", "iPhone 13", BigDecimal.valueOf(1200));
		ProductRequest productRequest2 = getProductRequest("Samsung Galaxy S21", "Samsung Galaxy S21", BigDecimal.valueOf(1000));

		Product product1 = new Product();
		product1.setName(productRequest1.getName());
		product1.setDescription(productRequest1.getDescription());
		product1.setPrice(productRequest1.getPrice());
		productRepository.save(product1);

		Product product2 = new Product();
		product2.setName(productRequest2.getName());
		product2.setDescription(productRequest2.getDescription());
		product2.setPrice(productRequest2.getPrice());
		productRepository.save(product2);

		// Fetch all products via GET request
		ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].name").value(productRequest1.getName()))
				.andExpect(jsonPath("$[0].description").value(productRequest1.getDescription()))
				.andExpect(jsonPath("$[0].price").value(productRequest1.getPrice().intValue()))
				.andExpect(jsonPath("$[1].name").value(productRequest2.getName()))
				.andExpect(jsonPath("$[1].description").value(productRequest2.getDescription()))
				.andExpect(jsonPath("$[1].price").value(productRequest2.getPrice().intValue()));

		// Capture the response body to inspect the JSON
		String responseBody = resultActions.andReturn().getResponse().getContentAsString();
		System.out.println("Response JSON: " + responseBody);

		// Optionally, parse and log specific JSONPath values
		String name1 = JsonPath.parse(responseBody).read("$[0].name");
		String name2 = JsonPath.parse(responseBody).read("$[1].name");
		System.out.println("First Product Name: " + name1);
		System.out.println("Second Product Name: " + name2);
	}
}
