package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Hasta Veri Erişim Katmanı (Patient Repository)
 */
@Repository // Spring Data JPA component olarak işaretler
public interface PatientRepository extends JpaRepository<Patient, Integer> {

}
