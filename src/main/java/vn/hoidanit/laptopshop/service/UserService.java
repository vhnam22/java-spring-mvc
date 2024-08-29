package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public String handleHello() {
        return "Hello from service";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsersByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void handleSaveUser(User user) {
        userRepository.save(user);
    }

    public User getUserById(long id) {
        return userRepository.findFirstById(id);
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }
}
