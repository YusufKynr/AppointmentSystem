package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Kullanıcı REST Controller (User API Endpoints)
 * 
 * Bu sınıf kullanıcı yönetimi için HTTP API endpoint'lerini sağlar.
 * RESTful API standartlarına uygun olarak tasarlanmıştır.
 * 
 * REST API Design Patterns:
 * - GET /user/{id} - Resource'u ID ile getir
 * - GET /user/getAll - Collection'ı listele
 * - POST /user/register - Yeni resource oluştur
 * - PUT /user/update/{id} - Resource'u güncelle
 * - DELETE /user/delete/{id} - Resource'u sil
 * 
 * CORS Configuration:
 * @CrossOrigin React frontend'in bu API'yi çağırabilmesi için gerekli.
 * Production'da daha spesifik domain'ler belirtilmeli.
 */
@RestController // @Controller + @ResponseBody - REST API controller
@RequestMapping("/user") // Base path: tüm endpoint'ler /user ile başlar
@CrossOrigin(origins = "http://localhost:3000") // React frontend için CORS
public class UserController {
    
    // Dependency Injection - Service katmanı ile bağlantı
    private final UserService userService;

    /**
     * Constructor Injection
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Kullanıcı Bilgilerini ID ile Getirme
     * 
     * @param id Aranacak kullanıcının ID'si (URL path'den alınır)
     * @return ResponseEntity<User> - 200 OK (user bulundu) veya 404 Not Found
     * 
     * HTTP Endpoint: GET /user/getUser/{id}
     * Örnek URL: GET /user/getUser/123
     */
    @GetMapping("/getUser/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok) // User bulunduysa 200 OK döndür
                .orElse(ResponseEntity.notFound().build()); // Bulunamadıysa 404 döndür
    }

    /**
     * Tüm Kullanıcıları Listeleme
     * 
     * @return ResponseEntity<List<User>> - 200 OK ile kullanıcı listesi
     * 
     * HTTP Endpoint: GET /user/getAllUser
     *
     */
    @GetMapping("/getAllUser")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Tüm Doktorları Listeleme
     * 
     * @return ResponseEntity<List<Doctor>> - 200 OK ile doktor listesi
     * 
     * HTTP Endpoint: GET /user/getAllDoctors
     */
    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(userService.getAllDoctors());
    }

    /**
     * Tüm Hastaları Listeleme
     * 
     * @return ResponseEntity<List<Patient>> - 200 OK ile hasta listesi
     * 
     * HTTP Endpoint: GET /user/getAllPatients
     *
     */
    @GetMapping("/getAllPatients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(userService.getAllPatients());
    }

    /**
     * Uzmanlık Alanına Göre Doktor Filtreleme
     * 
     * @param specialty Aranacak uzmanlık alanı (URL path'den String olarak alınır)
     * @return ResponseEntity<List<Doctor>> - 200 OK (doktor listesi) veya 400 Bad Request
     * 
     * HTTP Endpoint: GET /user/getDoctorsBySpecialty/{specialty}
     * Örnek URL: GET /user/getDoctorsBySpecialty/Cardiology
     * 
     * Desteklenen specialty değerleri:
     * - Dermatology, Cardiology, Eye, General_Surgery
     *
     */
    @GetMapping("/getDoctorsBySpecialty/{specialty}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialty(@PathVariable String specialty) {
        try {
            // String'i enum'a çevir (case-sensitive)
            Doctor.Specialty spec = Doctor.Specialty.valueOf(specialty);
            return ResponseEntity.ok(userService.getDoctorsBySpecialty(spec));
        } catch (IllegalArgumentException e) {
            // Geçersiz specialty değeri için 400 Bad Request
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Kullanıcı Bilgilerini Güncelleme
     * 
     * @param id Güncellenecek kullanıcının ID'si
     * @param updatedUser Güncellenmiş kullanıcı bilgileri (JSON request body'den alınır)
     * @return ResponseEntity<User> - 200 OK (güncellendi) veya 404 Not Found
     * 
     * HTTP Endpoint: PUT /user/update/{id}
     * Request Body: JSON formatında User objesi
     *
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            // Kullanıcı bulunamazsa Service'den RuntimeException fırlar
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Genel Kullanıcı Kayıt İşlemi
     * 
     * @param user Kaydedilecek kullanıcı bilgileri (JSON request body)
     * @return ResponseEntity<User> - 200 OK (başarılı) veya 400 Bad Request (hata)
     * 
     * HTTP Endpoint: POST /user/register
     * Request Body: {"email": "user@example.com", "password": "123456", "role": "PATIENT"}
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user.getEmail(), user.getPassword(), user.getRole());
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            // Email already exists gibi business logic hataları
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Doktor Kayıt İşlemi (Specialized Registration)
     * 
     * @param requestData Doktor kayıt bilgileri (Map formatında JSON)
     * @return ResponseEntity<Doctor> - 200 OK (başarılı) veya 400 Bad Request (hata)
     * 
     * HTTP Endpoint: POST /user/registerDoctor
     * Request Body: {
     *   "email": "doctor@hospital.com",
     *   "password": "securepass",
     *   "name": "Ahmet", 
     *   "surname": "Yılmaz",
     *   "birthDate": "1980-05-15",
     *   "phoneNo": "+905551234567",
     *   "specialty": "Cardiology"
     * }
     *
     */
    @PostMapping("/registerDoctor")
    public ResponseEntity<Doctor> registerDoctor(@RequestBody Map<String, Object> requestData) {
        try {
            // JSON field'larını extract et
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            String name = (String) requestData.get("name");
            String surname = (String) requestData.get("surname");
            String birthDateStr = (String) requestData.get("birthDate");
            String phoneNo = (String) requestData.get("phoneNo");
            String specialtyStr = (String) requestData.get("specialty");
            
            // String to LocalDate conversion (ISO format: YYYY-MM-DD)
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            // String to Enum conversion
            Doctor.Specialty specialty = Doctor.Specialty.valueOf(specialtyStr);
            
            // Service method call
            Doctor doctor = userService.registerDoctor(email, password, name, surname, birthDate, phoneNo, specialty);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            // Parsing errors, business logic errors, validation errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Hasta Kayıt İşlemi (Specialized Registration)
     * 
     * @param requestData Hasta kayıt bilgileri (Map formatında JSON)
     * @return ResponseEntity<Patient> - 200 OK (başarılı) veya 400 Bad Request (hata)
     * 
     * HTTP Endpoint: POST /user/registerPatient
     * Request Body: {
     *   "email": "patient@gmail.com",
     *   "password": "mypassword",
     *   "name": "Ayşe",
     *   "surname": "Kaya", 
     *   "birthDate": "1995-03-20",
     *   "phoneNo": "+905559876543"
     * }
     *
     */
    @PostMapping("/registerPatient")
    public ResponseEntity<Patient> registerPatient(@RequestBody Map<String, Object> requestData) {
        try {
            // JSON parsing
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            String name = (String) requestData.get("name");
            String surname = (String) requestData.get("surname");
            String birthDateStr = (String) requestData.get("birthDate");
            String phoneNo = (String) requestData.get("phoneNo");
            
            // Data conversion
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            
            // Service call
            Patient patient = userService.registerPatient(email, password, name, surname, birthDate, phoneNo);
            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Kullanıcı Giriş İşlemi (Authentication)
     * 
     * @param user Giriş bilgileri (email ve password içeren JSON)
     * @return ResponseEntity<User> - 200 OK (başarılı giriş) veya 401 Unauthorized (hatalı giriş)
     * 
     * HTTP Endpoint: POST /user/login
     * Request Body: {"email": "user@example.com", "password": "userpassword"}
     *
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        try {
            User loggedInUser = userService.login(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(loggedInUser);
        } catch (RuntimeException e) {
            // "Kullanıcı bulunamadı" veya "Şifre yanlış" hataları
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Kullanıcı Silme İşlemi
     * 
     * @param id Silinecek kullanıcının ID'si
     * @return ResponseEntity<String> - 200 OK (başarıyla silindi) veya 404 Not Found (bulunamadı)
     * 
     * HTTP Endpoint: DELETE /user/delete/{id}
     *
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Kullanıcı silindi");
        } catch (RuntimeException e) {
            // Kullanıcı bulunamadı
            return ResponseEntity.notFound().build();
        }
    }
}
