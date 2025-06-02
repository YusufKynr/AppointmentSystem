package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Appointment;
import com.egebilmuh.medicaltracking.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<Appointment> createAppoinment(@RequestParam int doctorId, @RequestParam int patientId, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime appointmentTime) {
        Appointment appointment = appointmentService.createAppointment(doctorId, patientId, appointmentTime);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/doctor/{id}")
    public List<Appointment> getAppointmentsByDoctor(@PathVariable int id) {
        return appointmentService.getAppointmentsByDoctor(id);
    }


    @GetMapping("/patient/{id}")
    public List<Appointment> getAppointmentsByPatient(@PathVariable int id) {
        return appointmentService.getAppointmentsByPatient(id);
    }


    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<String> cancelAppointment(@PathVariable int id) {
        appointmentService.cancelAppointment(id);
        return ResponseEntity.ok("Randevu iptal edildi");
    }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approveAppointment(@PathVariable int id) {
        appointmentService.approveAppointment(id);
        return ResponseEntity.ok("Randevu onaylandı");
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> rejectAppointment(@PathVariable int id) {
        appointmentService.approveAppointment(id);
        return ResponseEntity.ok("Randevu reddedildi");
    }

    @PostMapping("/setNote/{id}")
    public ResponseEntity<String> setNoteAppoinment(@PathVariable int id) {
        appointmentService.approveAppointment(id);
        return ResponseEntity.ok("Randevu reddedildi");
    }





}
