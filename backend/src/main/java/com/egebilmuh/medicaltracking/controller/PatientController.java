package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Hasta REST Controller (Patient API Endpoints)
 * 
 * Bu sınıf hasta-specific HTTP API endpoint'lerini sağlar.
 * Patient entity'si için özelleştirilmiş web layer işlemlerini yönetir.
 *
 */
@RestController // REST API controller olarak işaretler
@RequestMapping("/patient") // Base path: /patient
public class PatientController {
    
    // Dependency Injection - Service layer bağlantısı
    private final PatientService patientService;

    /**
     * Constructor Injection
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Tüm Hastaları Listeleme
     * 
     * @return ResponseEntity<List<Patient>> - 200 OK ile hasta listesi
     * 
     * HTTP Endpoint: GET /patient/getAllPatients
     */
    @GetMapping("/getAllPatients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * Hasta Bilgilerini Güncelleme
     * 
     * @param id Güncellenecek hastanın ID'si (URL path variable)
     * @param updatedPatient Güncellenmiş hasta bilgileri (JSON request body)
     * @return ResponseEntity<Patient> - 200 OK ile güncellenmiş hasta bilgileri
     * 
     * HTTP Endpoint: PUT /patient/update/{id}
     * Request Body: JSON formatında Patient objesi
     *
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody Patient updatedPatient) {
        // TODO: Try-catch block ekle, appropriate HTTP status codes döndür
        Patient patient = patientService.updatePatient(id, updatedPatient);
        return ResponseEntity.ok(patient);
    }
}
