package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Randevu Varlık Sınıfı (Appointment Entity)
 * 
 * Bu sınıf doktor-hasta randevularını temsil eder.
 * Veritabanında 'appointment' tablosunu oluşturur.
 * 
 * İlişkiler (Relationships):
 * - Doctor ile Many-to-One ilişki (Bir doktorun birçok randevusu olabilir)
 * - Patient ile Many-to-One ilişki (Bir hastanın birçok randevusu olabilir)
 * 
 * Öğrenci Notu: Bu sınıf entity'ler arası ilişkilerin nasıl kurulduğunu gösterir.
 * @ManyToOne anotasyonu ile foreign key ilişkileri tanımlanır.
 */
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor (JPA için zorunlu)
@AllArgsConstructor // Lombok: Tüm parametreleri alan constructor
@Entity // JPA anotasyonu: Bu sınıfın veritabanı entity'si olduğunu belirtir
public class Appointment {
    
    /**
     * Randevu Benzersiz Kimliği (Primary Key)
     * 
     * @Id - Birincil anahtar olduğunu belirtir
     * @GeneratedValue - Otomatik değer üretimi
     * IDENTITY stratejisi - Veritabanının auto-increment özelliğini kullanır
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    /**
     * Randevunun Atandığı Doktor
     * 
     * @ManyToOne - Çoka-Bir ilişki (Bir doktorun birçok randevusu olabilir)
     * 
     * Öğrenci Notu: JPA otomatik olarak foreign key oluşturur.
     * Veritabanında 'doctor_user_id' sütunu olarak saklanır.
     * Lazy loading varsayılan olarak açıktır (performans için).
     */
    @ManyToOne
    private Doctor doctor;
    
    /**
     * Randevuyu Alan Hasta
     * 
     * @ManyToOne - Çoka-Bir ilişki (Bir hastanın birçok randevusu olabilir)
     * 
     * Foreign key ilişkisi ile patient tablosuna referans verir.
     */
    @ManyToOne
    private Patient patient;

    /**
     * Randevu Durumu Enum'u
     * 
     * Randevunun mevcut durumunu belirtir:
     * - PENDING: Beklemede (henüz onaylanmamış)
     * - CONFIRMED: Onaylanmış (kesin randevu)
     * - CANCELLED: İptal edilmiş
     * 
     * Öğrenci Notu: Enum kullanarak randevu durumlarını kontrol altında tutarız.
     * Bu, geçersiz durumların girilmesini önler.
     */
    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED
    };

    /**
     * Randevunun Mevcut Durumu
     * 
     * @Enumerated(EnumType.STRING) - Enum değerini string olarak saklar
     * 
     * Veritabanında 'PENDING', 'CONFIRMED' veya 'CANCELLED' olarak saklanır.
     */
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    /**
     * Randevu Tarih ve Saati
     * 
     * LocalDateTime tipinde saklanır - hem tarih hem saat bilgisi içerir.
     * 
     * Öğrenci Notu: LocalDateTime immutable'dır ve thread-safe'dir.
     * Format: YYYY-MM-DDTHH:MM:SS (ISO 8601 standardı)
     * Örnek: 2024-01-15T14:30:00 (15 Ocak 2024, 14:30)
     */
    private LocalDateTime appointmentDateTime;

    /**
     * Doktor Notu
     * 
     * Doktorun randevu ile ilgili yazdığı not.
     * Tanı, tedavi önerileri veya genel notlar içerebilir.
     * 
     * String tipinde - uzun metinler içerebilir.
     * Veritabanında TEXT sütunu olarak saklanır.
     */
    private String doctorNote;

}
