package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;

    public Patient getPatient(int patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta kaydı bulunamadı"));
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient updatePatient(int id, Patient updatedPatient) {
        Patient existPatient = getPatient(id);
        if (updatedPatient.getName() != null) {
            existPatient.setName(updatedPatient.getName());
        }
        if (updatedPatient.getSurname() != null) {
            existPatient.setSurname(updatedPatient.getSurname());
        }
        if (updatedPatient.getBirthDate() != null) {
            existPatient.setBirthDate(updatedPatient.getBirthDate());
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
