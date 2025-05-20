package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Doctor")
public class DoctorController {
    private final DoctorService DoctorService;

    public DoctorController(DoctorService DoctorService) {
        this.DoctorService = DoctorService;
    }

    @GetMapping("/getDoctor/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable int id) {
        return ResponseEntity.ok(DoctorService.getDoctor(id));
    }

    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(DoctorService.getAllDoctors());
    }



}
