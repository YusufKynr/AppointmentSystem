package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Doktor Veri Erişim Katmanı (Doctor Repository)
 * 
 * Bu interface Doctor entity'si için özelleştirilmiş
 * veritabanı sorgularını içerir.
 * 
 * Öğrenci Notu: JpaRepository<Doctor, Integer> şu anlama gelir:
 * - Doctor: İşlenecek entity tipi
 * - Integer: Primary key'in veri tipi (userId)
 * 
 * Generic repository'den gelen temel CRUD işlemlerine ek olarak
 * Doctor'a özel query metodları tanımlanmıştır.
 */
@Repository // Spring Data JPA repository component olarak işaretler
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
    
    /**
     * Uzmanlık Alanına Göre Doktor Filtreleme
     * 
     * @param specialty Aranacak uzmanlık alanı (Dermatology, Cardiology vs.)
     * @return List<Doctor> - Belirtilen uzmanlığa sahip doktorların listesi
     * 
     * Öğrenci Notu: Method name query creation kullanılır.
     * "findBySpecialty" otomatik olarak şu SQL'e dönüşür:
     * SELECT * FROM doctor WHERE specialty = ?
     * 
     * Hasta doktor seçerken uzmanlık alanına göre filtreleme için kullanılır.
     */
    List<Doctor> findBySpecialty(Doctor.Specialty specialty);
    
    /**
     * Müsait Doktorları Listeleme
     * 
     * @return List<Doctor> - Randevu kabul eden (availability=true) doktorlar
     * 
     * Öğrenci Notu: "findByAvailabilityTrue" method naming convention ile
     * otomatik olarak şu SQL'e dönüşür:
     * SELECT * FROM doctor WHERE availability = true
     * 
     * Sadece aktif olarak randevu veren doktorları göstermek için kullanılır.
     */
    List<Doctor> findByAvailabilityTrue();
    
    /**
     * Uzmanlık Alanı ve Müsaitlik Durumuna Göre Filtreleme
     * 
     * @param specialty Aranacak uzmanlık alanı
     * @return List<Doctor> - Belirtilen uzmanlığa sahip VE müsait doktorlar
     * 
     * Öğrenci Notu: Multiple condition'lar "And" ile birleştirilebilir.
     * Bu metod şu SQL'e dönüşür:
     * SELECT * FROM doctor WHERE specialty = ? AND availability = true
     * 
     * En optimize doktor arama sorgusu - hem uzmanlık hem müsaitlik kontrolü.
     */
    List<Doctor> findBySpecialtyAndAvailabilityTrue(Doctor.Specialty specialty);
}
