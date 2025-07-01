package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Order(3)
@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserSeeder(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder)
    {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {this.createUser();}

    private void createUser() {
        User userbasic = new User();
        userbasic.setName("User");
        userbasic.setLastname("User");
        userbasic.setEmail("user@user.com");
        userbasic.setPassword("user123");

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        Optional<User> optionalUser = userRepository.findByEmail(userbasic.getEmail());

        if(optionalRole.isEmpty() || optionalUser.isPresent()){
            return;
        }

        var user = new User();
        user.setName(userbasic.getName());
        user.setLastname(userbasic.getLastname());
        user.setEmail(userbasic.getEmail());
        user.setPassword(passwordEncoder.encode(userbasic.getPassword()));
        user.setRole(optionalRole.get());

        userRepository.save(user);
    }
}
