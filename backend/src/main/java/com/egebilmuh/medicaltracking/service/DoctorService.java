package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Doktor İş Mantığı Servisi (Doctor Service)
 * 
 * Bu sınıf doktorlara özel business logic işlemlerini içerir.
 * UserService'den farklı olarak sadece Doctor entity'si ile çalışır.
 * 
 * Sorumluluklar:
 * - Doktor bilgilerini getirme
 * - Doktor listelerini yönetme
 * - Doktor-specific business logic (gelecekte eklenebilir)
 *
 */
@Service // Spring Service component olarak işaretler
@RequiredArgsConstructor // Lombok: final field'lar için constructor injection
public class DoctorService {
    
    // Dependency Injection - Constructor injection ile
    private final DoctorRepository doctorRepository;

    /**
     * ID ile Doktor Bilgisini Getirme
     * 
     * @param doctorId Aranacak doktorun ID'si
     * @return Doctor - Bulunan doktor nesnesi
     * @throws RuntimeException Doktor bulunamazsa
     */
    public Doctor getDoctor(int doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
    }

    /**
     * Tüm Doktorları Listeleme
     * 
     * @return List<Doctor> - Sistemdeki tüm doktorların listesi

     */
    public List<Doctor> getAllDoctors(){
        return doctorRepository.findAll();
    }

}
