package com.kindconnect.service;

import com.kindconnect.model.Type;
import com.kindconnect.repository.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TypeService {

    private final TypeRepository typeRepository;

    @Autowired
    public TypeService(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public Optional<Type> getTypeById(Long id) {
        return typeRepository.findById(id);
    }

    public Optional<Type> getTypeByCode(String code) {
        return typeRepository.findByCode(code);
    }

    public boolean existsByCode(String code) {
        return typeRepository.existsByCode(code);
    }

    @Transactional
    public Type createType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    public Type updateType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }

    public long countAll() {
        return typeRepository.count();
    }
}
