package com.example.currency4.service;

import com.example.currency4.entity.User;
import com.example.currency4.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(value = "userCache")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "userCache", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @CachePut(value = "userCache", key = "#result.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @CachePut(value = "userCache", key = "#id")
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        user.setUsername(userDetails.getUsername());
        return userRepository.save(user);
    }

    @CacheEvict(value = "userCache", key = "#id")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        userRepository.delete(user);
    }
}