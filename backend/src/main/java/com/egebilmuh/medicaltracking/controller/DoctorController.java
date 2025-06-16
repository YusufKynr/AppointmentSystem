package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.service.DoctorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Doktor REST Controller (Doctor API Endpoints)
 * 
 * Bu sınıf doktor-specific HTTP API endpoint'lerini sağlar.
 * UserController'dan farklı olarak sadece Doctor entity'si ile çalışır.
 * 
 * Sorumluluklar:
 * - Doktor bilgilerini getirme API'leri
 * - Doktor listelerini döndürme
 * - Doktor-specific işlemler (gelecekte eklenebilir)
 * 
 * Öğrenci Notu: Bu controller specialized approach kullanır.
 * UserController generic approach kullanırken, bu controller
 * sadece doktor işlemlerine odaklanır.
 * 
 * Mimari Karar: İki yaklaşım da doğru:
 * 1. Tek controller (UserController) - Basit projeler için
 * 2. Specialized controllers - Büyük projeler için
 * 
 * Base URL: /Doctor (not RESTful - /doctors olmalıydı)
 */
@RestController // REST API controller olarak işaretler
@RequestMapping("/Doctor") // Base path - tüm endpoint'ler /Doctor ile başlar
public class DoctorController {
    
    // Dependency Injection - Service layer ile bağlantı
    private final DoctorService DoctorService; // Naming convention: doctorService olmalıydı
    
    /**
     * Constructor Injection
     * 
     * @param DoctorService Doktor business logic service'i
     * 
     * Öğrenci Notu: Variable naming Java convention'ını ihlal ediyor.
     * 'doctorService' (camelCase) olmalı, 'DoctorService' (PascalCase) değil.
     */
    public DoctorController(DoctorService DoctorService) {
        this.DoctorService = DoctorService;
    }

    /**
     * ID ile Doktor Bilgisini Getirme
     * 
     * @param id Aranacak doktorun ID'si (URL path variable)
     * @return ResponseEntity<Doctor> - 200 OK ile doktor bilgileri
     * 
     * HTTP Endpoint: GET /Doctor/getDoctor/{id}
     * Örnek URL: GET /Doctor/getDoctor/123
     * 
     * Öğrenci Notu: Bu endpoint UserController'daki getUser'a benzer.
     * Fark: DoctorService.getDoctor() RuntimeException fırlatır,
     * Optional döndürmez. Bu yüzden exception handling eksik!
     * 
     * Potential Problem: Doktor bulunamazsa 500 Internal Server Error döner,
     * 404 Not Found dönmesi daha doğru olurdu.
     */
    @GetMapping("/getDoctor/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable int id) {
        // TODO: Exception handling ekle (doktor bulunamazsa 404 döndür)
        return ResponseEntity.ok(DoctorService.getDoctor(id));
    }

    /**
     * Tüm Doktorları Listeleme
     * 
     * @return ResponseEntity<List<Doctor>> - 200 OK ile doktor listesi
     * 
     * HTTP Endpoint: GET /Doctor/getAllDoctors
     * 
     * Kullanım: Admin paneli veya hasta randevu alma sistemi için.
     * 
     * Öğrenci Notu: Bu endpoint UserController'daki getAllDoctors ile aynı işlevi görür.
     * Code duplication var - bir tanesini kullanmak daha iyi olurdu.
     * 
     * Mimari Öneri: Tek bir endpoint kullan, birden fazla yoldan aynı veriye erişim
     * kafa karışıklığına neden olabilir.
     */
    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(DoctorService.getAllDoctors());
    }
    
    // Gelecekte eklenebilecek doctor-specific endpoint'ler:
    
    /**
     * Doktor Müsaitlik Durumunu Güncelleme
     * (Gelecekte implement edilebilir)
     * 
     * @PutMapping("/updateAvailability/{id}")
     * public ResponseEntity<Doctor> updateAvailability(@PathVariable int id, @RequestParam boolean available) {
     *     Doctor doctor = DoctorService.updateAvailability(id, available);
     *     return ResponseEntity.ok(doctor);
     * }
     */
    
    /**
     * Doktor Randevu Programını Getirme
     * (Gelecekte implement edilebilir)
     * 
     * @GetMapping("/schedule/{id}")
     * public ResponseEntity<List<Appointment>> getDoctorSchedule(@PathVariable int id, @RequestParam String date) {
     *     LocalDate scheduleDate = LocalDate.parse(date);
     *     List<Appointment> appointments = appointmentService.getDoctorAppointments(id, scheduleDate);
     *     return ResponseEntity.ok(appointments);
     * }
     */

}
