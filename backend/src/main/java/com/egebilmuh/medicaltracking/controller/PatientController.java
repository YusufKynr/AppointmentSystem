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
 * Sorumluluklar:
 * - Hasta bilgilerini listeleme
 * - Hasta bilgilerini güncelleme  
 * - Hasta-specific HTTP operasyonları
 * 
 * Öğrenci Notu: Bu controller minimal implementation içerir.
 * UserController'da daha kapsamlı hasta işlemleri var.
 * Bu durum code duplication'a neden oluyor.
 * 
 * Mimari İyileştirme Önerisi:
 * - Ya PatientController'ı genişlet, UserController'daki hasta metodları kaldır
 * - Ya da PatientController'ı tamamen kaldır, her şeyi UserController'da yap
 * 
 * RESTful Naming: "/patient" doğru, "/patients" daha RESTful olurdu
 */
@RestController // REST API controller olarak işaretler
@RequestMapping("/patient") // Base path: /patient
public class PatientController {
    
    // Dependency Injection - Service layer bağlantısı
    private final PatientService patientService;

    /**
     * Constructor Injection
     * 
     * @param patientService Hasta business logic service'i
     * 
     * Öğrenci Notu: Bu controller'da naming convention doğru uygulanmış.
     * DoctorController'daki hatalı naming'den farklı.
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
     * 
     * Öğrenci Notu: Bu endpoint UserController'da da var (code duplication).
     * İki endpoint aynı işi yapıyor:
     * - GET /user/getAllPatients
     * - GET /patient/getAllPatients
     * 
     * Problem: Hangi endpoint'i kullanacağı belirsiz.
     * Solution: Tek bir endpoint kullan, API documentation'da belirt.
     * 
     * Security Note: Hasta listesi sensitive data içerir.
     * GDPR compliance için access control gerekli.
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
     * Öğrenci Notu: Exception handling eksik!
     * PatientService.updatePatient() RuntimeException fırlatabilir:
     * - Hasta bulunamazsa ("Hasta kaydı bulunamadı")
     * 
     * Missing Error Handling:
     * - 404 Not Found: Hasta bulunamazsa
     * - 400 Bad Request: Invalid data gönderilirse
     * 
     * Şu anda exception fırlarsa 500 Internal Server Error döner,
     * uygun HTTP status code'ları döndürmeli.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable int id, @RequestBody Patient updatedPatient) {
        // TODO: Try-catch block ekle, appropriate HTTP status codes döndür
        Patient patient = patientService.updatePatient(id, updatedPatient);
        return ResponseEntity.ok(patient);
    }
    
    // Gelecekte eklenebilecek hasta-specific endpoint'ler:
    
    /**
     * Hasta Detay Bilgilerini Getirme
     * (Implement edilmeli - şu anda eksik)
     * 
     * @GetMapping("/{id}")
     * public ResponseEntity<Patient> getPatient(@PathVariable int id) {
     *     try {
     *         Patient patient = patientService.getPatient(id);
     *         return ResponseEntity.ok(patient);
     *     } catch (RuntimeException e) {
     *         return ResponseEntity.notFound().build();
     *     }
     * }
     */
    
    /**
     * Hasta Randevu Geçmişini Getirme
     * (Gelecekte implement edilebilir)
     * 
     * @GetMapping("/{id}/appointments")
     * public ResponseEntity<List<Appointment>> getPatientAppointments(@PathVariable int id) {
     *     List<Appointment> appointments = appointmentService.getPatientAppointments(id);
     *     return ResponseEntity.ok(appointments);
     * }
     */
    
    /**
     * Yeni Hasta Kaydı
     * (UserController'da var, buraya da eklenebilir)
     * 
     * @PostMapping("/register")
     * public ResponseEntity<Patient> registerPatient(@RequestBody PatientRegistrationRequest request) {
     *     Patient patient = patientService.registerNewPatient(request);
     *     return ResponseEntity.status(HttpStatus.CREATED).body(patient);
     * }
     */

}
