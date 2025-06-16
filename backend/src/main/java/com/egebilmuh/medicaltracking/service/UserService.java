package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import com.egebilmuh.medicaltracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Kullanıcı İş Mantığı Servisi (User Service)
 * 
 * Bu sınıf kullanıcı yönetimi ile ilgili tüm business logic'i içerir.
 * Repository pattern kullanarak veri erişim katmanından ayrılmıştır.
 * 
 * Sorumluluklar:
 * - Kullanıcı CRUD işlemleri (Create, Read, Update, Delete)
 * - Doktor ve Hasta kayıt işlemleri
 * - Şifre hashleme ve doğrulama
 * - Login authentication logic'i
 * - Kullanıcı validasyon işlemleri
 * 
 * Öğrenci Notu: Service Layer, Controller ile Repository arasında yer alır.
 * Business logic burada yazılır, Controller sadece HTTP isteklerini yönetir.
 * 
 * Kullanılan Design Pattern'ler:
 * - Dependency Injection (@RequiredArgsConstructor ile constructor injection)
 * - Repository Pattern (UserRepository, DoctorRepository, PatientRepository)
 * - Service Layer Pattern (Business logic separation)
 */
@Service // Spring'e bu sınıfın bir Service component'i olduğunu belirtir
@RequiredArgsConstructor // Lombok: final field'lar için constructor oluşturur (DI için)
public class UserService {
    
    // Dependency Injection - Constructor injection ile güvenli DI
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder; // SecurityConfig'den inject edilir

    /**
     * ID ile Kullanıcı Bulma
     * 
     * @param userId Aranacak kullanıcının ID'si
     * @return Optional<User> - Kullanıcı bulunursa dolu, bulunamazsa boş Optional
     * 
     * Öğrenci Notu: Optional kullanımı null check'lerini ortadan kaldırır.
     * Controller katmanında .orElse() veya .map() ile kullanılabilir.
     */
    public Optional<User> getUser(int userId) {
        return userRepository.findById(userId);
    }

    /**
     * Tüm Kullanıcıları Listeleme
     * 
     * @return List<User> - Sistemdeki tüm kullanıcıların listesi
     * 
     * Öğrenci Notu: Bu metod admin paneli için kullanılabilir.
     * Production'da pagination eklenmeli (sayfa sayfa listeleme).
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Tüm Doktorları Listeleme
     * 
     * @return List<Doctor> - Sistemdeki tüm doktorların listesi
     * 
     * Kullanım: Hasta panelinde doktor seçimi için kullanılır.
     * Frontend'de dropdown veya liste halinde gösterilir.
     */
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    /**
     * Tüm Hastaları Listeleme
     * 
     * @return List<Patient> - Sistemdeki tüm hastaların listesi
     * 
     * Kullanım: Admin paneli veya doktor panelinde hasta listesi.
     * GDPR compliance için hasta listesi erişimi kısıtlanmalı.
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Uzmanlık Alanına Göre Doktor Filtreleme
     * 
     * @param specialty Aranacak uzmanlık alanı (Dermatology, Cardiology vs.)
     * @return List<Doctor> - Belirtilen uzmanlığa sahip doktorlar
     * 
     * Öğrenci Notu: Bu metod Repository'deki custom query'yi kullanır.
     * Business logic yok, sadece delegation (yönlendirme) var.
     */
    public List<Doctor> getDoctorsBySpecialty(Doctor.Specialty specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    /**
     * Kullanıcı Bilgilerini Güncelleme
     * 
     * @param id Güncellenecek kullanıcının ID'si
     * @param updatedUser Güncellenmiş kullanıcı bilgileri
     * @return User - Güncellenmiş kullanıcı nesnesi
     * 
     * Öğrenci Notu: Partial update pattern kullanılır.
     * Sadece null olmayan field'lar güncellenir.
     * Şifre güncellenmeden önce hash'lenir (güvenlik).
     */
    public User updateUser(int id, User updatedUser) {
        // Mevcut kullanıcıyı bul, yoksa exception fırlat
        User existUser = getUser(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: id=" + id));
        
        // Şifre güncellenecekse hash'le (güvenlik)
        if (updatedUser.getPassword() != null) {
            existUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        
        // E-posta güncellenecekse ata
        if (updatedUser.getEmail() != null) {
            existUser.setEmail(updatedUser.getEmail());
        }

        return userRepository.save(existUser);
    }

    /**
     * Yeni Kullanıcı Kaydetme (Generic)
     * 
     * @param user Kaydedilecek kullanıcı nesnesi
     * @return User - Kaydedilmiş kullanıcı (ID ile birlikte)
     * 
     * Öğrenci Notu: Bu metod duplicate ID kontrolü yapar.
     * Normalde ID auto-generate olduğu için bu kontrol gereksizdir,
     * ancak manual ID assignment durumları için koruma sağlar.
     */
    public User saveUser(User user) {
        if (userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("Kullanıcı zaten mevcut: id=" + user.getUserId());
        }
        return userRepository.save(user);
    }

    /**
     * Kullanıcı Silme İşlemi
     * 
     * @param userId Silinecek kullanıcının ID'si
     * 
     * Öğrenci Notu: Soft delete yerine hard delete kullanılır.
     * Production'da genellikle soft delete tercih edilir:
     * - Kullanıcı "deleted" field'ı true yapılır
     * - Veri fiziksel olarak silinmez
     * - GDPR compliance için "right to be forgotten" düşünülmeli
     */
    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Kullanıcı bulunamadı: id=" + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * Genel Kullanıcı Kayıt İşlemi
     * 
     * @param email Kullanıcının e-posta adresi (unique olmalı)
     * @param password Düz metin şifre (hash'lenecek)
     * @param role Kullanıcı rolü (DOCTOR veya PATIENT)
     * @return User - Kaydedilmiş kullanıcı nesnesi
     * 
     * Öğrenci Notu: Bu metod temel User kayıt işlemi yapar.
     * Doktor ve Hasta kayıtları için özelleştirilmiş metodlar vardır.
     * Business logic: Email uniqueness kontrolü + şifre hashleme
     */
    public User register(String email, String password, User.Role role) {
        // Duplicate email kontrolü (business rule)
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        // Şifreyi güvenli hash'le (security requirement)
        String hashedPassword = passwordEncoder.encode(password);

        // Yeni kullanıcı nesnesi oluştur ve field'ları ata
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);

        return userRepository.save(newUser);
    }

    /**
     * Doktor Kayıt İşlemi (Specialized Registration)
     * 
     * @param email Doktorun e-posta adresi
     * @param password Şifre (plain text - hash'lenecek)
     * @param name Doktorun adı
     * @param surname Doktorun soyadı
     * @param birthDate Doğum tarihi
     * @param phoneNo Telefon numarası
     * @param specialty Uzmanlık alanı
     * @return Doctor - Kaydedilmiş doktor nesnesi
     * 
     * Öğrenci Notu: Bu metod inheritance kullanır.
     * Doctor extends User olduğu için hem User hem Doctor field'ları doldurulur.
     * availability varsayılan olarak true yapılır (yeni doktor aktif).
     */
    public Doctor registerDoctor(String email, String password, String name, String surname, 
                                LocalDate birthDate, String phoneNo, Doctor.Specialty specialty) {
        // Email uniqueness kontrolü (tüm kullanıcılar için)
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        // Güvenlik: Şifreyi hash'le
        String hashedPassword = passwordEncoder.encode(password);

        // Doktor nesnesi oluştur ve tüm field'ları doldur
        Doctor doctor = new Doctor();
        // User field'ları (inheritance)
        doctor.setEmail(email);
        doctor.setPassword(hashedPassword);
        doctor.setRole(User.Role.DOCTOR); // Role otomatik DOCTOR
        // Doctor-specific field'lar
        doctor.setName(name);
        doctor.setSurname(surname);
        doctor.setBirthDate(birthDate);
        doctor.setPhoneNo(phoneNo);
        doctor.setSpecialty(specialty);
        doctor.setAvailability(true); // Yeni doktor aktif olarak başlar

        return doctorRepository.save(doctor);
    }

    /**
     * Hasta Kayıt İşlemi (Specialized Registration)
     * 
     * @param email Hastanın e-posta adresi
     * @param password Şifre (plain text - hash'lenecek)
     * @param name Hastanın adı
     * @param surname Hastanın soyadı
     * @param birthDate Doğum tarihi
     * @param phoneNo Telefon numarası
     * @return Patient - Kaydedilmiş hasta nesnesi
     * 
     * Öğrenci Notu: Doctor registration'a benzer ancak daha basit.
     * Patient'ta specialty ve availability field'ları yok.
     * Role otomatik olarak PATIENT atanır.
     */
    public Patient registerPatient(String email, String password, String name, String surname,
                                  LocalDate birthDate, String phoneNo) {
        // Email uniqueness kontrolü
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        // Şifre güvenliği
        String hashedPassword = passwordEncoder.encode(password);

        // Hasta nesnesi oluştur
        Patient patient = new Patient();
        // User field'ları (inheritance)
        patient.setEmail(email);
        patient.setPassword(hashedPassword);
        patient.setRole(User.Role.PATIENT); // Role otomatik PATIENT
        // Patient-specific field'lar
        patient.setName(name);
        patient.setSurname(surname);
        patient.setBirthDate(birthDate);
        patient.setPhoneNo(phoneNo);

        return patientRepository.save(patient);
    }

    /**
     * Kullanıcı Giriş İşlemi (Authentication)
     * 
     * @param email Giriş yapacak kullanıcının e-postası
     * @param password Düz metin şifre
     * @return User - Giriş yapmış kullanıcı nesnesi
     * 
     * Öğrenci Notu: Authentication işlemi iki adımlı:
     * 1. Email ile kullanıcı bulma (identification)
     * 2. Şifre doğrulama (authentication)
     * 
     * BCrypt.matches() metodu plain text şifreyi hash ile karşılaştırır.
     * Hash'i decode etmez, aynı algoritma ile tekrar hash'leyip karşılaştırır.
     */
    public User login(String email, String password){
        // 1. Adım: Email ile kullanıcı bul
        User existUser = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new RuntimeException("Kullanıcı bulunamadı"));
        
        // 2. Adım: Şifre doğrulama (BCrypt comparison)
        if(!passwordEncoder.matches(password, existUser.getPassword())){
            throw new RuntimeException("Şifre yanlış");
        }
        
        // Authentication başarılı - kullanıcı nesnesini döndür
        return existUser;
    }
}