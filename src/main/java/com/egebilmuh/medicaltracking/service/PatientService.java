package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public Optional<Patient> getPatient(int patientId) {
        return patientRepository.findById(patientId);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient updatePatient(int id, Patient updatedPatient) {
        Patient existPatient = getPatient(id)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı: id=" + id));
        if (updatedPatient.getName() != null) {
            existPatient.setName(updatedPatient.getName());
        }
        if (updatedPatient.getSurname() != null) {
            existPatient.setSurname(updatedPatient.getSurname());
        }
        if (updatedPatient.getAddress() != null) {
            existPatient.setAddress(updatedPatient.getAddress());
        }
        if (updatedPatient.getPhoneNo() != null) {
            existPatient.setPhoneNo(updatedPatient.getPhoneNo());
        }
        if (updatedPatient.getRole() != null) {
            existPatient.setRole(updatedPatient.getRole());
        }
        return patientRepository.save(existPatient);
    }

    public Patient savePatient(Patient patient) {
        if (patientRepository.existsById(patient.getUserId())) {
            throw new IllegalArgumentException("Kullanıcı zaten mevcut: id=" + patient.getUserId());
        }
        return patientRepository.save(patient);
    }

    public void deletePatient(int userId) {
        if (!patientRepository.existsById(userId)) {
            throw new RuntimeException("Kullanıcı bulunamadı: id=" + userId);
        }
        patientRepository.deleteById(userId);
    }
}
