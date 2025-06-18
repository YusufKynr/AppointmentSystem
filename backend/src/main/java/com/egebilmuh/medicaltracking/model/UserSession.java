package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Kullanıcı Oturum Varlık Sınıfı (User Session Entity)
 * 
 * Bu sınıf kullanıcıların aktif oturumlarını takip eder.
 * Web uygulamalarında session management için kullanılır.
 * 
 * Önemli Özellikler:
 * - Her giriş yapan kullanıcı için session kaydı oluşturulur
 * - Session token ile kullanıcı kimliği doğrulanır
 * - Oturum süresi takip edilir (expires_at)
 * - Güvenlik için IP adresi ve user agent bilgileri saklanır
 *
 */
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor (JPA için zorunlu)
@AllArgsConstructor // Lombok: Tüm parametreleri alan constructor
@Entity // JPA anotasyonu: Bu sınıfın veritabanı entity'si olduğunu belirtir
public class UserSession {
    
    /**
     * Session Benzersiz Kimliği (Primary Key)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    /**
     * Session'ın Ait Olduğu Kullanıcı
     * 
     * @ManyToOne - Çoka-Bir ilişki (Bir kullanıcının birçok session'ı olabilir)
     * @JoinColumn - Foreign key sütun adını özelleştirme
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Session Token (Oturum Anahtarı)
     */
    private String sessionToken;
    
    /**
     * Session Oluşturulma Zamanı
     */
    private LocalDateTime createdAt;
    
    /**
     * Session Bitiş Zamanı
     * 
     * Session'ın ne zaman süresinin dolacağını belirtir.
     * Bu tarihten sonra session geçersiz sayılır.
     */
    private LocalDateTime expiresAt;
    
    /**
     * Session Aktiflik Durumu
     * true: Session aktif
     * false: Session pasif/sonlandırılmış
     */
    private boolean isActive;
    
    /**
     * Kullanıcı Tarayıcı Bilgisi (User Agent)
     * Örnek: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
     */
    private String userAgent;
    
    /**
     * Bağlantı IP Adresi
     */
    private String ipAddress;

    /**
     * Özel Constructor - Yeni Session Oluşturma
     * 
     * @param user Session'ın ait olduğu kullanıcı
     * @param sessionToken Benzersiz session anahtarı
     * @param expiresAt Session'ın bitiş zamanı
     */
    public UserSession(User user, String sessionToken, LocalDateTime expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.createdAt = LocalDateTime.now(); // Şu anki zaman
        this.expiresAt = expiresAt;
        this.isActive = true; // Yeni session aktif olarak başlar
    }
} 