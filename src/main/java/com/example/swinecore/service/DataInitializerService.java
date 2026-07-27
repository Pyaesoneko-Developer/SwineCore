package com.example.swinecore.service;

import com.example.swinecore.entity.Genetics;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.GeneticsRepository;
import com.example.swinecore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializerService {

    private final UserRepository userRepository;
    private final GeneticsRepository geneticsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void init() {
        seedAdmin();
        seedDefaultGenetics();
    }

    private void seedAdmin() {
        if (!userRepository.existsByEmail("admin@swinecore.mm")) {
            User admin = User.builder()
                .name("Admin")
                .email("admin@swinecore.mm")
                .password(passwordEncoder.encode("123123"))
                .role(Role.ADMIN)
                .startDate(LocalDate.now())
                .enabled(true)
                .mustChangePassword(false)
                .build();
            userRepository.save(admin);
            log.info("Default admin account created: admin@swinecore.com / Admin@1234");
        }
    }

    private void seedDefaultGenetics() {
        if (geneticsRepository.count() == 0) {
            List<Genetics> defaults = List.of(
                Genetics.builder().name("Yorkshire").code("Y").description("Large White — excellent maternal traits").active(true).build(),
                Genetics.builder().name("Duroc").code("D").description("High growth rate, excellent meat quality").active(true).build(),
                Genetics.builder().name("Landrace").code("L").description("Long body, high litter size").active(true).build(),
                Genetics.builder().name("Pietrain").code("P").description("Lean muscle, stress-susceptible").active(true).build(),
                Genetics.builder().name("Yorkshire x Landrace").code("YL").description("F1 cross — good mothering").active(true).build(),
                Genetics.builder().name("Landrace x Yorkshire").code("LY").description("F1 cross — high productivity").active(true).build(),
                Genetics.builder().name("Duroc x Yorkshire x Landrace").code("DYL").description("Terminal sire cross").active(true).build()
            );
            geneticsRepository.saveAll(defaults);
            log.info("Default pig genetics seeded.");
        }
    }
}
