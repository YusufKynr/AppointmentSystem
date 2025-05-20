package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository doctorRepository;

    public Doctor getDoctor(int doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunumadı"));
    }

    public List<Doctor> getAllDoctors(){
        return doctorRepository.findAll();
    }
}
