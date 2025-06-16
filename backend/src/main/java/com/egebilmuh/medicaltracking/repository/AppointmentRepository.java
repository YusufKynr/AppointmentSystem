package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Randevu Veri Erişim Katmanı (Appointment Repository)
 * 
 * Bu interface Appointment entity'si için özelleştirilmiş
 * veritabanı sorgularını içerir.
 * 
 * Önemli Özellikler:
 * - Çakışan randevu kontrolü (aynı doktor, aynı saat)
 * - Hasta bazlı randevu listeleme
 * - Doktor bazlı randevu listeleme
 * 
 * Öğrenci Notu: Randevu sistemi karmaşık business logic içerir.
 * Repository'de sadece veri erişim sorguları yer alır,
 * business logic Service katmanında yapılır.
 */
@Repository // Spring Data JPA component olarak işaretler
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {

    /**
     * Çakışan Randevu Kontrolü
     * 
     * @param doctor Randevu veren doktor
     * @param appointmentDateTime Randevu tarih-saati
     * @return boolean - Çakışma varsa true, yoksa false
     * 
     * Öğrenci Notu: "existsByDoctorAndAppointmentDateTime" method naming ile
     * otomatik olarak şu SQL'e dönüşür:
     * SELECT COUNT(*) > 0 FROM appointment 
     * WHERE doctor_user_id = ? AND appointment_date_time = ?
     * 
     * Kullanım: Yeni randevu oluştururken aynı doktorun aynı saatte
     * başka randevusu olup olmadığını kontrol eder.
     * Bu, double booking'i (çakışan randevu) önler.
     */
    boolean existsByDoctorAndAppointmentDateTime(Doctor doctor, LocalDateTime appointmentDateTime);

    /**
     * Hastanın Tüm Randevularını Listeleme
     * 
     * @param patient Randevuları sorgulanacak hasta
     * @return List<Appointment> - Hastanın tüm randevularının listesi
     * 
     * Öğrenci Notu: "findByPatient" method naming ile şu SQL'e dönüşür:
     * SELECT * FROM appointment WHERE patient_user_id = ?
     * 
     * Kullanım: Hasta panelinde geçmiş ve gelecek randevuları göstermek için.
     * Tarih sıralaması yapmak için Service katmanında Collections.sort() kullanılabilir.
     */
    List<Appointment> findByPatient(Patient patient);
    
    /**
     * Doktorun Tüm Randevularını Listeleme
     * 
     * @param doctor Randevuları sorgulanacak doktor
     * @return List<Appointment> - Doktorun tüm randevularının listesi
     * 
     * Öğrenci Notu: "findByDoctor" method naming ile şu SQL'e dönüşür:
     * SELECT * FROM appointment WHERE doctor_user_id = ?
     * 
     * Kullanım: Doktor panelinde günlük/haftalık randevu programını göstermek için.
     * Doktor kendi randevularını yönetebilir (onaylama, iptal etme vs.).
     */
    List<Appointment> findByDoctor(Doctor doctor);
    
    // Gelecekte eklenebilecek yararlı metodlar:
    // List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    // List<Appointment> findByStatus(AppointmentStatus status);
    // List<Appointment> findByDoctorAndAppointmentDateTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    // List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);
}
