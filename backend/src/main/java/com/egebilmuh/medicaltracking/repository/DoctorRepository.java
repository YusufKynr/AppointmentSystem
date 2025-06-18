package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Doktor Veri Erişim Katmanı (Doctor Repository)
 */
@Repository // Spring Data JPA repository component olarak işaretler
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
    
    /**
     * Uzmanlık Alanına Göre Doktor Filtreleme
    List<Doctor> findBySpecialty(Doctor.Specialty specialty);
    
    /**
     * Müsait Doktorları Listeleme
     * 
     * @return List<Doctor> - Randevu kabul eden (availability=true) doktorlar
     */
    List<Doctor> findByAvailabilityTrue();
    
    /**
     * Uzmanlık Alanı ve Müsaitlik Durumuna Göre Filtreleme
     * 
     * @param specialty Aranacak uzmanlık alanı
     * @return List<Doctor> - Belirtilen uzmanlığa sahip VE müsait doktorlar
     */
    List<Doctor> findBySpecialtyAndAvailabilityTrue(Doctor.Specialty specialty);
}
