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
 *
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
     */
    public Appointment addDoctorNote(int appointmentId, String doctorNote) {
        Appointment appointment = getAppointment(appointmentId);
        appointment.setDoctorNote(doctorNote);
        return appointmentRepository.save(appointment);
    }

    /**
     * ID ile Randevu Getirme
     */
    public Appointment getAppointment(int appointmentId) {
        return appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Randevu bulunamadı"));
    }

    /**
     * Tüm Randevuları Listeleme
     */
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    /**
     * Hastanın Randevularını Getirme
     */
    public List<Appointment> getPatientAppointments(int patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Hasta bulunamadı"));
        return appointmentRepository.findByPatient(patient);
    }

    /**
     * Doktorun Randevularını Getirme
     */
    public List<Appointment> getDoctorAppointments(int doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doktor bulunamadı"));
        return appointmentRepository.findByDoctor(doctor);
    }

    /**
     * Randevu Silme İşlemi
     */
    public void deleteAppointment(int appointmentId) {
        if (!appointmentRepository.existsById(appointmentId)) {
            throw new RuntimeException("Randevu bulunamadı");
        }
        appointmentRepository.deleteById(appointmentId);
    }

    /**
     * Randevuya Not Ekleme - Controller tarafından kullanılan metod adı
     */
    public Appointment setNoteToAppointment(int appointmentId, String doctorNote) {
        return addDoctorNote(appointmentId, doctorNote);
    }

    /**
     * Doktor ID'sine göre randevuları getirme - Controller tarafından kullanılan metod adı
     */
    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return getDoctorAppointments(doctorId);
    }

    /**
     * Hasta ID'sine göre randevuları getirme - Controller tarafından kullanılan metod adı
     */
    public List<Appointment> getAppointmentsByPatient(int patientId) {
        return getPatientAppointments(patientId);
    }

    /**
     * Randevuyu İptal Etme
     */
    public Appointment cancelAppointment(int appointmentId) {
        return updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.CANCELLED);
    }

    /**
     * Randevuyu Onaylama
     */
    public Appointment approveAppointment(int appointmentId) {
        return updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.CONFIRMED);
    }

    /**
     * Randevuyu Reddetme
     */
    public Appointment rejectAppointment(int appointmentId) {
        return updateAppointmentStatus(appointmentId, Appointment.AppointmentStatus.CANCELLED);
    }
}
