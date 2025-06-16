package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Hasta Veri Erişim Katmanı (Patient Repository)
 * 
 * Bu interface Patient entity'si için veritabanı işlemlerini sağlar.
 * Şu anda sadece temel CRUD işlemleri kullanılıyor.
 * 
 * Öğrenci Notu: Repository interface'ler minimal olarak başlar.
 * İhtiyaç duyuldukça özel query metodları eklenir.
 * 
 * JpaRepository'den otomatik gelen metodlar:
 * - save(patient) - Hasta kaydetme/güncelleme
 * - findById(id) - ID ile hasta bulma
 * - findAll() - Tüm hastaları listeleme
 * - delete(patient) - Hasta silme
 * - count() - Toplam hasta sayısı
 * 
 * Gelecekte eklenebilecek özel metodlar:
 * - findByName(String name) - Ada göre arama
 * - findByPhoneNo(String phone) - Telefona göre arama
 * - findByBirthDateBetween(LocalDate start, LocalDate end) - Yaş aralığı
 */
@Repository // Spring Data JPA component olarak işaretler
public interface PatientRepository extends JpaRepository<Patient, Integer> {
    
    // Şu anda özel query metodları yok
    // İhtiyaç duyuldukça buraya eklenecek
    
    // Örnek gelecekteki metodlar:
    // List<Patient> findByNameContainingIgnoreCase(String name);
    // Optional<Patient> findByPhoneNo(String phoneNo);
    // List<Patient> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);
}
