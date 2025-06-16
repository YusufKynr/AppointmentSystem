package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.AppointmentRepository;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Randevu İş Mantığı Servisi (Appointment Service)
 * 
 * Bu sınıf randevu yönetimi ile ilgili tüm business logic'i içerir.
 * Medikal takip sisteminin core functionality'si buradadır.
 * 
 * Ana Sorumluluklar:
 * - Randevu oluşturma ve validation
 * - Çakışan randevu kontrolü (double booking prevention)
 * - Randevu durumu yönetimi (PENDING, CONFIRMED, CANCELLED)
 * - Doktor ve hasta bazlı randevu listeleme
 * - Randevu business rules enforcement
 * 
 * Öğrenci Notu: Bu service karmaşık business logic içerir:
 * - Temporal logic (zaman çakışmaları)
 * - Entity relationships (Doctor, Patient, Appointment)
 * - State management (appointment status)
 * - Validation rules (business constraints)
 * 
 * Design Patterns:
 * - Service Layer Pattern: Business logic encapsulation
 * - Repository Pattern: Data access abstraction
 * - Dependency Injection: Loose coupling
 */
@Service // Spring Service component olarak işaretler
@RequiredArgsConstructor // Lombok: Constructor injection için
public class AppointmentService {
    
    // Dependency Injection - Repository pattern dependencies
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    /**
     * Yeni Randevu Oluşturma (Core Business Logic)
     * 
     * @param doctorId Randevu veren doktorun ID'si
     * @param patientId Randevu alan hastanın ID'si  
     * @param appointmentDateTime Randevu tarih ve saati
     * @return Appointment - Oluşturulan randevu nesnesi
     * @throws RuntimeException Business rule ihlali durumunda
     * 
     * Business Rules (Validation Logic):
     * 1. Doktor ve hasta mutlaka mevcut olmalı
     * 2. Aynı doktorda aynı saatte çakışan randevu olmamalı
     * 3. Randevu tarihi gelecekte olmalı (geçmiş tarih kabul edilmez)
     * 4. Doktor müsait olmalı (availability = true)
     * 
     * Öğrenci Notu: Bu metod transaction-safe olmalı.
     * Concurrent access durumunda race condition oluşabilir.
     * Database-level unique constraint veya pessimistic locking gerekebilir.
     */
    public Appointment createAppointment(int doctorId, int patientId, LocalDateTime appointmentDateTime) {
        // Business Rule 1: Entity existence validation
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));

        // Business Rule 2: Double booking prevention
        if (appointmentRepository.existsByDoctorAndAppointmentDateTime(doctor, appointmentDateTime)) {
            throw new RuntimeException("Bu saatte doktor başka bir randevuda");
        }

        // Business Rule 3: Future date validation
        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Geçmiş tarihte randevu alınamaz");
        }

        // Business Rule 4: Doctor availability check
        if (!doctor.isAvailability()) {
            throw new RuntimeException("Doktor şu anda randevu kabul etmiyor");
        }

        // Create and save appointment
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDateTime(appointmentDateTime);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING); // Default status
        appointment.setDoctorNote(null); // Will be filled later by doctor

        return appointmentRepository.save(appointment);
    }

    /**
     * Randevu Durumu Güncelleme (State Management)
     * 
     * @param appointmentId Güncellenmek istenen randevunun ID'si
     * @param status Yeni randevu durumu
     * @return Appointment - Güncellenmiş randevu
     * 
     * State Transitions (İzin verilen durum değişiklikleri):
     * - PENDING -> CONFIRMED (Doktor onayı)
     * - PENDING -> CANCELLED (İptal işlemi)
     * - CONFIRMED -> CANCELLED (Son dakika iptali)
     * 
     * Öğrenci Notu: State machine pattern uygulanabilir.
     * Geçersiz state transition'ları engellemek için validation eklenebilir.
     */
    public Appointment updateAppointmentStatus(int appointmentId, Appointment.AppointmentStatus status) {
        Appointment appointment = getAppointment(appointmentId);
        
        // State transition validation (gelecekte eklenebilir)
        // validateStateTransition(appointment.getStatus(), status);
        
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    /**
     * Doktor Notu Ekleme/Güncelleme
     * 
     * @param appointmentId Randevunun ID'si
     * @param doctorNote Doktor tarafından yazılan not
     * @return Appointment - Güncellenmiş randevu
     * 
     * Öğrenci Notu: Bu metod authorization check içermelidir.
     * Sadece ilgili doktor kendi randevusuna not ekleyebilmeli.
     * Security annotation'ları (@PreAuthorize) kullanılabilir.
     */
    public Appointment addDoctorNote(int appointmentId, String doctorNote) {
        Appointment appointment = getAppointment(appointmentId);
        appointment.setDoctorNote(doctorNote);
        return appointmentRepository.save(appointment);
    }

    /**
     * ID ile Randevu Getirme
     * 
     * @param appointmentId Aranacak randevunun ID'si
     * @return Appointment - Bulunan randevu nesnesi
     * @throws RuntimeException Randevu bulunamazsa
     */
    public Appointment getAppointment(int appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
    }

    /**
     * Tüm Randevuları Listeleme
     * 
     * @return List<Appointment> - Sistemdeki tüm randevular
     * 
     * Öğrenci Notu: Bu metod admin paneli için kullanılabilir.
     * Production'da pagination ve filtering eklenmeli.
     */
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * Hastanın Randevularını Getirme
     * 
     * @param patientId Hastanın ID'si
     * @return List<Appointment> - Hastanın tüm randevuları
     * 
     * Kullanım: Hasta panelinde geçmiş ve gelecek randevuları göstermek için.
     */
    public List<Appointment> getPatientAppointments(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        return appointmentRepository.findByPatient(patient);
    }

    /**
     * Doktorun Randevularını Getirme
     * 
     * @param doctorId Doktorun ID'si
     * @return List<Appointment> - Doktorun tüm randevuları
     * 
     * Kullanım: Doktor panelinde günlük/haftalık program göstermek için.
     */
    public List<Appointment> getDoctorAppointments(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        return appointmentRepository.findByDoctor(doctor);
    }

    /**
     * Randevu Silme İşlemi
     * 
     * @param appointmentId Silinecek randevunun ID'si
     * 
     * Öğrenci Notu: Hard delete yerine status'u CANCELLED yapmak daha iyi olabilir.
     * Audit trail ve historical data için silme işlemi log'lanmalı.
     */
    public void deleteAppointment(int appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new RuntimeException("Randevu bulunamadı");
        }
        appointmentRepository.deleteById(appointmentId);
    }
    
    // Gelecekte eklenebilecek advanced functionality metodları:
    
    /**
     * Doktor Müsait Saatleri Hesaplama
     * (Gelecekte implement edilebilir)
     * 
     * public List<LocalDateTime> getAvailableSlots(int doctorId, LocalDate date) {
     *     // Doktorun çalışma saatlerini al
     *     // Mevcut randevuları çıkar  
     *     // Müsait saatleri hesapla ve döndür
     * }
     */
}
