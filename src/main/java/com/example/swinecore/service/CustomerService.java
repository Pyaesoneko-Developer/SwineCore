package com.example.swinecore.service;

import com.example.swinecore.entity.CustomerAccount;
import com.example.swinecore.repository.CustomerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {

    private final CustomerAccountRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerAccount register(CustomerAccount customer) {
        if (customer.getName() == null || customer.getName().isBlank())
            throw new IllegalArgumentException("Customer name is required.");
        if (customer.getEmail() == null || customer.getEmail().isBlank())
            throw new IllegalArgumentException("Customer email is required.");
        customer.setName(customer.getName().trim());
        customer.setEmail(customer.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        if (customerRepository.existsByEmail(customer.getEmail()))
            throw new IllegalArgumentException("Email already registered: " + customer.getEmail());
        UserService.validateStrongPassword(customer.getPassword());
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setEnabled(true);
        return customerRepository.saveAndFlush(customer);
    }

    public Optional<CustomerAccount> findByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<CustomerAccount> findById(Long id) {
        return customerRepository.findById(id);
    }

    public CustomerAccount save(CustomerAccount customer) {
        return customerRepository.save(customer);
    }

    public List<CustomerAccount> findAll() { return customerRepository.findAll(); }

    public void restrict(Long id, String status, String reason) {
        if (!"SUSPENDED".equals(status) && !"BANNED".equals(status))
            throw new IllegalArgumentException("Unsupported customer status.");
        if (reason == null || reason.isBlank()) throw new IllegalArgumentException("A reason is required.");
        if (reason.trim().length() > 100) throw new IllegalArgumentException("Reason must be 100 characters or fewer.");
        CustomerAccount c = customerRepository.findById(id).orElseThrow();
        String hash = originalPasswordHash(c.getPassword());
        String encodedReason = Base64.getUrlEncoder().withoutPadding()
            .encodeToString(reason.trim().getBytes(StandardCharsets.UTF_8));
        c.setPassword("LOCK::" + status + "::" + encodedReason + "::" + hash);
        c.setEnabled(false);
        customerRepository.save(c);
    }

    public void activate(Long id) {
        CustomerAccount c = customerRepository.findById(id).orElseThrow();
        c.setPassword(originalPasswordHash(c.getPassword()));
        c.setEnabled(true);
        customerRepository.save(c);
    }

    public String statusOf(CustomerAccount c) {
        if (c.isEnabled()) return "ACTIVE";
        String[] parts = c.getPassword().split("::", 4);
        return parts.length == 4 && "LOCK".equals(parts[0]) ? parts[1] : "SUSPENDED";
    }

    public String restrictionReason(CustomerAccount c) {
        String[] parts = c.getPassword().split("::", 4);
        if (parts.length != 4 || !"LOCK".equals(parts[0])) return "Account disabled by administrator.";
        try { return new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8); }
        catch (Exception e) { return "Account disabled by administrator."; }
    }

    private String originalPasswordHash(String value) {
        String[] parts = value.split("::", 4);
        return parts.length == 4 && "LOCK".equals(parts[0]) ? parts[3] : value;
    }
}
