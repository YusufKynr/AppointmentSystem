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
 * Öğrenci Notu: Service katmanında specialization yapılabilir.
 * UserService genel kullanıcı işlemleri yaparken,
 * DoctorService doktor-specific işlemlere odaklanır.
 * 
 * Bu yaklaşımın avantajları:
 * - Single Responsibility Principle (SRP)
 * - Kod organizasyonu daha temiz
 * - Doktor-specific logic kolayca eklenebilir
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
     * 
     * Öğrenci Notu: Bu metod Optional.orElseThrow() pattern kullanır.
     * UserService'teki getUser()'dan farklı olarak Optional döndürmez,
     * direkt exception fırlatır. Bu, kullanım şekline göre tercih edilir.
     * 
     * İki yaklaşım:
     * 1. Optional döndür - caller karar versin (UserService yaklaşımı)
     * 2. Exception fırlat - business rule olarak kabul et (bu yaklaşım)
     */
    public Doctor getDoctor(int doctorId) {
        return doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
    }

    /**
     * Tüm Doktorları Listeleme
     * 
     * @return List<Doctor> - Sistemdeki tüm doktorların listesi
     * 
     * Öğrenci Notu: Bu metod UserService.getAllDoctors() ile aynı işlevi görür.
     * İki seçenek:
     * 1. UserService'i kullan (delegation pattern)
     * 2. Bu metodu kullan (direct repository access)
     * 
     * Hangisini kullanacağınız mimari tercihimize bağlı.
     * Consistency için tek yaklaşım seçilmeli.
     */
    public List<Doctor> getAllDoctors(){
        return doctorRepository.findAll();
    }
    
    // Gelecekte eklenebilecek doktor-specific metodlar:
    
    /**
     * Doktor Müsaitlik Durumunu Güncelleme
     * (Gelecekte implement edilebilir)
     * 
     * public Doctor updateAvailability(int doctorId, boolean availability) {
     *     Doctor doctor = getDoctor(doctorId);
     *     doctor.setAvailability(availability);
     *     return doctorRepository.save(doctor);
     * }
     */
    
    /**
     * Doktor Randevu Saatlerini Yönetme
     * (Gelecekte implement edilebilir)
     * 
     * public List<LocalDateTime> getAvailableSlots(int doctorId, LocalDate date) {
     *     // Doktorun müsait olduğu saatleri hesapla
     *     // Mevcut randevuları çıkar
     *     // Çalışma saatleri ile karşılaştır
     * }
     */
}
