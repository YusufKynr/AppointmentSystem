package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Appointment createAppointment(int doctorId, int patientId, LocalDateTime appointmentTime) {
        Doctor existDoctor = doctorService.getDoctor(doctorId);
        Patient existPatient = patientService.getPatient(patientId);
        if (appointmentRepository.existsByDoctorAndAppointmentDateTime(existDoctor, appointmentTime)) {
            throw new RuntimeException("Doktor bu saatte başka bir randevuya sahip");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(existDoctor);
        appointment.setPatient(existPatient);
        appointment.setAppointmentDateTime(appointmentTime);
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        Patient patient = patientService.getPatient(patientId);
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        Doctor doctor = doctorService.getDoctor(doctorId);
        return appointmentRepository.findByDoctor(doctor);
    }

    public void cancelAppointment(int appointmentId){
        if(!appointmentRepository.existsById(appointmentId)){
            throw new RuntimeException("Randevu bulunamadı: id=" + appointmentId);
        }
        appointmentRepository.deleteById(appointmentId);
    }

}
