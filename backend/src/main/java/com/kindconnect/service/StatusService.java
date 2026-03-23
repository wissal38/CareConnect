package com.kindconnect.service;

import com.kindconnect.model.Status;
import com.kindconnect.repository.StatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StatusService {

    private final StatusRepository statusRepository;

    @Autowired
    public StatusService(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    public List<Status> getAllStatus() {
        return statusRepository.findAll();
    }

    public Optional<Status> getStatusById(Long id) {
        return statusRepository.findById(id);
    }

    public Optional<Status> getStatusByCode(String code) {
        return statusRepository.findByCode(code);
    }

    public boolean existsByCode(String code) {
        return statusRepository.existsByCode(code);
    }

    @Transactional
    public Status createStatus(Status status) {
        return statusRepository.save(status);
    }

    @Transactional
    public Status updateStatus(Status status) {
        return statusRepository.save(status);
    }

    @Transactional
    public void deleteStatus(Long id) {
        statusRepository.deleteById(id);
    }

    public long countAll() {
        return statusRepository.count();
    }
}
