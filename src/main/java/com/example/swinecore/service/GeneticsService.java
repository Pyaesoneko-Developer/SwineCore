package com.example.swinecore.service;

import com.example.swinecore.entity.Genetics;
import com.example.swinecore.repository.GeneticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class GeneticsService {

    private final GeneticsRepository geneticsRepository;

    public Genetics create(Genetics genetics) {
        if (geneticsRepository.existsByCode(genetics.getCode()))
            throw new IllegalArgumentException("Genetics code already exists: " + genetics.getCode());
        return geneticsRepository.save(genetics);
    }

    public List<Genetics> findAll() {
        return geneticsRepository.findAll();
    }

    public List<Genetics> findActive() {
        return geneticsRepository.findByActiveTrue();
    }

    public Optional<Genetics> findById(Long id) {
        return geneticsRepository.findById(id);
    }

    public Genetics save(Genetics genetics) {
        return geneticsRepository.save(genetics);
    }

    public void delete(Long id) {
        geneticsRepository.deleteById(id);
    }
}
