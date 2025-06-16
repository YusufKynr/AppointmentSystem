package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Hasta İş Mantığı Servisi (Patient Service)
 * 
 * Bu sınıf hastalara özel business logic işlemlerini içerir.
 * Patient entity'si için CRUD işlemleri ve özel business kuralları yönetir.
 * 
 * Sorumluluklar:
 * - Hasta bilgilerini getirme ve güncelleme
 * - Hasta CRUD işlemleri (Create, Read, Update, Delete)
 * - Hasta-specific business logic
 * - Hasta bilgi validasyonları
 * 
 * Öğrenci Notu: Bu service UserService'den farklı olarak
 * sadece Patient entity'si ile çalışır. Özelleştirilmiş business logic
 * burada implement edilir.
 * 
 * Design Pattern: Service Layer Pattern
 * - Business logic'i Controller'dan ayırır
 * - Repository'ye delegation yapar
 * - Transaction management sağlar
 */
@Service // Spring Service component olarak işaretler
@RequiredArgsConstructor // Lombok: Constructor injection için
public class PatientService {
    
    // Dependency Injection - Repository pattern
    private final PatientRepository patientRepository;

    /**
     * ID ile Hasta Bilgisini Getirme
     * 
     * @param patientId Aranacak hastanın ID'si
     * @return Patient - Bulunan hasta nesnesi
     * @throws RuntimeException Hasta bulunamazsa
     * 
     * Öğrenci Notu: Exception-first approach kullanılır.
     * Optional döndürmek yerine direkt exception fırlatılır.
     * Bu, business rule olarak "hasta mutlaka bulunmalı" anlamına gelir.
     */
    public Patient getPatient(int patientId) {
        return patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta kaydı bulunamadı"));
    }

    /**
     * Tüm Hastaları Listeleme
     * 
     * @return List<Patient> - Sistemdeki tüm hastaların listesi
     * 
     * Öğrenci Notu: Bu metod admin paneli veya doktor paneli için kullanılır.
     * Production'da:
     * - Pagination eklenmeli (çok hasta olabilir)
     * - Access control yapılmalı (GDPR compliance)
     * - Filtering seçenekleri sunulmalı
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Hasta Bilgilerini Güncelleme (Partial Update)
     * 
     * @param id Güncellenecek hastanın ID'si
     * @param updatedPatient Güncellenmiş hasta bilgileri
     * @return Patient - Güncellenmiş hasta nesnesi
     * 
     * Öğrenci Notu: Partial update pattern kullanılır.
     * Sadece null olmayan field'lar güncellenir.
     * Bu yaklaşım RESTful API'lerde PATCH metodu için uygundur.
     * 
     * Business Logic:
     * - Mevcut hasta mutlaka bulunmalı
     * - Sadece değişen field'lar güncellenir
     * - Tüm field'ların validation'ı yapılabilir (gelecekte)
     */
    public Patient updatePatient(int id, Patient updatedPatient) {
        // Mevcut hastayı bul (bulunamazsa exception)
        Patient existPatient = getPatient(id);
        
        // Partial update: sadece null olmayan field'ları güncelle
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
        
        // Güncellenmiş hasta nesnesini kaydet ve döndür
        return patientRepository.save(existPatient);
    }

    /**
     * Yeni Hasta Kaydetme
     * 
     * @param patient Kaydedilecek hasta nesnesi
     * @return Patient - Kaydedilmiş hasta (ID ile birlikte)
     * @throws IllegalArgumentException Hasta zaten mevcutsa
     * 
     * Öğrenci Notu: Bu metod duplicate kontrolü yapar.
     * Normalde UserService.registerPatient() kullanılmalı,
     * çünkü şifre hashleme logic'i orada var.
     * 
     * Bu metod direct Patient object kaydetme için kullanılır.
     */
    public Patient savePatient(Patient patient) {
        // Duplicate ID kontrolü (business rule)
        if (patientRepository.existsById(patient.getUserId())) {
            throw new IllegalArgumentException("Kullanıcı zaten mevcut: id=" + patient.getUserId());
        }
        return patientRepository.save(patient);
    }

    /**
     * Hasta Silme İşlemi
     * 
     * @param userId Silinecek hastanın ID'si
     * @throws RuntimeException Hasta bulunamazsa
     * 
     * Öğrenci Notu: Hard delete yapılır.
     * Production'da soft delete tercih edilir:
     * - "deleted" flag true yapılır
     * - Veri fiziksel olarak silinmez
     * - GDPR "right to be forgotten" düşünülmeli
     * 
     * Cascade Operations:
     * - Hastanın randevuları ne olacak?
     * - Foreign key constraints kontrol edilmeli
     */
    public void deletePatient(int userId) {
        // Hasta varlığını kontrol et
        if (!patientRepository.existsById(userId)) {
            throw new RuntimeException("Kullanıcı bulunamadı: id=" + userId);
        }
        // Hard delete gerçekleştir
        patientRepository.deleteById(userId);
    }
    
    // Gelecekte eklenebilecek hasta-specific metodlar:
    
    /**
     * Hasta Yaşını Hesaplama
     * (Gelecekte implement edilebilir)
     * 
     * public int calculateAge(int patientId) {
     *     Patient patient = getPatient(patientId);
     *     return Period.between(patient.getBirthDate(), LocalDate.now()).getYears();
     * }
     */
    
    /**
     * Hasta Randevu Geçmişini Getirme
     * (Gelecekte implement edilebilir)
     * 
     * public List<Appointment> getPatientAppointments(int patientId) {
     *     Patient patient = getPatient(patientId);
     *     return appointmentRepository.findByPatient(patient);
     * }
     */
}
