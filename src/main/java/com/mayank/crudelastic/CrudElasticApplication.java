package com.mayank.crudelastic;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mayank.crudelastic.config.JestClientService;
import com.mayank.crudelastic.model.User;
import com.mayank.crudelastic.repository.UserRepository;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Get;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;

@SpringBootApplication
@RestController
public class CrudElasticApplication {
	@Autowired
	private UserRepository repository;
	
	JestClient client =null;
	public JestClient getClient() {
		 if(this.client==null)
		 {
		 System.out.println("setting up connection with jedis");
		 JestClientFactory factory = new JestClientFactory();
		    factory.setHttpClientConfig(
		      new HttpClientConfig.Builder("https://search-ytsearch-staging-vflomzxcm3c4pklej6nwyomxfm.us-east-1.es.amazonaws.com")
		        .multiThreaded(true)
		        .defaultMaxTotalConnectionPerRoute(2)
		        .maxTotalConnection(10)
		        .build());
		    this.client=factory.getObject();
		    return factory.getObject();
		 }
		 return this.client;
	 
	 
	 }
	
	@PostMapping("/saveUser")
	public String saveCustomer(@RequestBody User users) throws IOException {
		
        JestClient client = this.getClient();
        
        ObjectMapper mapper = new ObjectMapper();
        JsonNode userNode = mapper.createObjectNode()
          .put("firstName", users.getFirstName())
          .put("lastName", users.getLastName())
          .put("age", users.getAge());
        JestResult postResult = client.execute(new Index.Builder(userNode.toString()).index("crud_user").type("user").build());
        
        return postResult.toString();
		/*repository.saveAll(users);
		return users.size();*/
	}

	@GetMapping("/find/{id}")
	public String findUser(@PathVariable final String id) throws IOException {	
		JestClient client = this.getClient();
		 	
		JestResult getResult = client.execute(new Get.Builder("crud_user",id).type("user").build());
		return getResult.toString();
	}
	
	@PutMapping("/update/{id}")
	public String updateUser(@PathVariable final String id ,@RequestBody User users) throws IOException
	{
		JestClient client = this.getClient();
		
		  ObjectMapper mapper = new ObjectMapper();
        	JsonNode userNode = mapper.createObjectNode()
          .put("firstName", users.getFirstName())
          .put("lastName", users.getLastName())
          .put("age", users.getAge());
        	JestResult putResult  = client.execute(new Update.Builder(userNode.toString()).index("crud_user").id(id).build());
		
		return putResult.toString();
	}
  @DeleteMapping("/delete/{id}")
  public String deleteUser(@PathVariable final String id, @RequestBody User users) throws IOException
  {	
	  JestClient client = this.getClient();
	  
	  JestResult deleteResult = client.execute(new Delete.Builder(id).index("crud_user").type("user").build());
	  return deleteResult.toString();
  }
	
	
	public static void main(String[] args) {
		SpringApplication.run(CrudElasticApplication.class, args);
	}

}
