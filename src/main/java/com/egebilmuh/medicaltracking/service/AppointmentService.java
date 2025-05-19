package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.repository.AppointmentRepository;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Appointment createAppointment(Doctor doctor, Patient patient, LocalDateTime time) {
        Optional<Doctor> existDoctor = doctorService.getDoctor(doctor.getUserId());
        Optional<Patient> existPatient = patientService.getPatient(patient.getUserId());
        if (existDoctor.isEmpty()) {
            throw new RuntimeException("Doktor bulunamadı");
        }
        if (existPatient.isEmpty()){
            throw new RuntimeException("Hasta bulunamadı");
        } else if (doctor.isAvailability()) {
            return null;
        }


        return null;
    }

    public Appointment saveAppointment(Appointment appointment) {
        if(appointmentRepository.existsById(appointment.getAppointmentId())){
            throw new IllegalArgumentException("Randevu zaten mevcut: id=" + appointment.getAppointmentId());
        }
        return appointmentRepository.save(appointment);
    }

}
