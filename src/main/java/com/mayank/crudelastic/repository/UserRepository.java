package com.mayank.crudelastic.repository;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.mayank.crudelastic.model.User;

public interface UserRepository extends ElasticsearchRepository<User, String> {
	List<User> findByFirstName(String firstName);
}
