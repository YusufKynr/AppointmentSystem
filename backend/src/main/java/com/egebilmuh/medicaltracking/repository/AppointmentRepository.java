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
     */
    boolean existsByDoctorAndAppointmentDateTime(Doctor doctor, LocalDateTime appointmentDateTime);

    /**
     * Hastanın Tüm Randevularını Listeleme
     * 
     * @param patient Randevuları sorgulanacak hasta
     * @return List<Appointment> - Hastanın tüm randevularının listesi
     */
    List<Appointment> findByPatient(Patient patient);
    
    /**
     * Doktorun Tüm Randevularını Listeleme
     * 
     * @param doctor Randevuları sorgulanacak doktor
     * @return List<Appointment> - Doktorun tüm randevularının listesi
     *
     */
    List<Appointment> findByDoctor(Doctor doctor);
    
    // Gelecekte eklenebilecek yararlı metodlar:
    // List<Appointment> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
    // List<Appointment> findByStatus(AppointmentStatus status);
    // List<Appointment> findByDoctorAndAppointmentDateTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    // List<Appointment> findByPatientAndStatus(Patient patient, AppointmentStatus status);
}
