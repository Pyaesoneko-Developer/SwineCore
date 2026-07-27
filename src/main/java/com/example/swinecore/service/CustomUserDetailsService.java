package com.example.swinecore.service;

import com.example.swinecore.entity.CustomerAccount;
import com.example.swinecore.entity.User;
import com.example.swinecore.entity.enums.Role;
import com.example.swinecore.repository.CustomerAccountRepository;
import com.example.swinecore.repository.UserRepository;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final CustomerAccountRepository customerRepository;

    public CustomUserDetailsService(UserRepository userRepository,
                                    CustomerAccountRepository customerRepository) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try internal staff users
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User u = userOpt.get();
            if (!u.isEnabled()) throw new DisabledException("Account is disabled");
            return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPassword(), u.isEnabled(),
                true, true, true,
                buildAuthorities(u.getRole())
            );
        }

        // Then try customer accounts
        Optional<CustomerAccount> custOpt = customerRepository.findByEmail(email);
        if (custOpt.isPresent()) {
            CustomerAccount c = custOpt.get();
            if (!c.isEnabled()) throw new DisabledException(restrictionMessage(c));
            return new org.springframework.security.core.userdetails.User(
                c.getEmail(), c.getPassword(), c.isEnabled(),
                true, true, true,
                buildAuthorities(Role.CUSTOMER)
            );
        }

        throw new UsernameNotFoundException("No account found for: " + email);
    }

    private String restrictionMessage(CustomerAccount c) {
        String[] parts = c.getPassword().split("::", 4);
        if (parts.length != 4 || !"LOCK".equals(parts[0])) return "SUSPENDED|Account disabled by administrator.";
        String reason;
        try { reason = new String(Base64.getUrlDecoder().decode(parts[2]), StandardCharsets.UTF_8); }
        catch (Exception e) { reason = "Contact SwineCore support."; }
        return parts[1] + "|" + reason;
    }

    private Collection<? extends GrantedAuthority> buildAuthorities(Role role) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
}
