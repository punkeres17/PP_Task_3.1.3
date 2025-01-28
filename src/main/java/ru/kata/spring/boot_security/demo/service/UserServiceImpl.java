package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(final Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with this %d not found", id)));

    }

    private String checkEncodePass(final String password) {
        if (!password.startsWith("$2a$")) {
            return passwordEncoder.encode(password);
        }
        return password;
    }

    @Override
    @Transactional
    public void saveUser(final User user) {
        user.setPassword(checkEncodePass(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(final User user) {
        user.setPassword(checkEncodePass(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void removeUserById(final Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findByUsername(final String username) {
        return userRepository.findByUsername(username);
    }
}
