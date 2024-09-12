package ru.scrile.org.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.scrile.org.model.User;
import ru.scrile.org.model.exception.UserAlreadyExistException;
import ru.scrile.org.repository.UserRepository;


@Service
public class UserService {

    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String password, int age) {
        if (userRepository.existsByName(username)) {
            throw new UserAlreadyExistException(username);
        }

        User user = User.builder()
                .name(username)
                .password(passwordEncoder.encode(password))
                .age(age)
                .build();

        userRepository.save(user);
    }

    public String authenticate(String username, String password) throws AuthenticationException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        return "Auth successful for user: " + username;
    }
}
