package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
@CrossOrigin(origins = "http://localhost:3000")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppointment(@RequestBody Map<String, Object> requestData) {
        try {
            int doctorId = (Integer) requestData.get("doctorId");
            int patientId = (Integer) requestData.get("patientId");
            String appointmentTimeStr = (String) requestData.get("appointmentTime");
            String doctorNote = (String) requestData.get("doctorNote");
            
            LocalDateTime appointmentTime = LocalDateTime.parse(appointmentTimeStr);
            
            Appointment appointment = appointmentService.createAppointment(doctorId, patientId, appointmentTime);
            if (doctorNote != null && !doctorNote.trim().isEmpty()) {
                appointmentService.setNoteToAppointment(appointment.getAppointmentId(), doctorNote);
            }
            
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/doctor/{id}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctor(@PathVariable int id) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(id);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatient(@PathVariable int id) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsByPatient(id);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable int id) {
        try {
            appointmentService.cancelAppointment(id);
            return ResponseEntity.ok("Randevu iptal edildi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Randevu iptal edilemedi: " + e.getMessage());
        }
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveAppointment(@PathVariable int id) {
        try {
            appointmentService.approveAppointment(id);
            return ResponseEntity.ok("Randevu onaylandı");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Randevu onaylanamadı: " + e.getMessage());
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectAppointment(@PathVariable int id) {
        try {
            appointmentService.rejectAppointment(id);
            return ResponseEntity.ok("Randevu reddedildi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Randevu reddedilemedi: " + e.getMessage());
        }
    }

    @PostMapping("/setNote/{id}")
    public ResponseEntity<String> setNoteAppointment(@PathVariable int id, @RequestBody Map<String, String> requestData) {
        try {
            String doctorNote = requestData.get("doctorNote");
            appointmentService.setNoteToAppointment(id, doctorNote);
            return ResponseEntity.ok("Randevu notu eklendi");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Randevu notu eklenemedi: " + e.getMessage());
        }
    }
}
