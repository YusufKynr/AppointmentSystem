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
 * Öğrenci Notu: Session management web güvenliğinin temel taşlarından biridir.
 * Stateless REST API'lerde genellikle JWT token kullanılır, ancak
 * session-based authentication de yaygın bir yöntemdir.
 */
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor (JPA için zorunlu)
@AllArgsConstructor // Lombok: Tüm parametreleri alan constructor
@Entity // JPA anotasyonu: Bu sınıfın veritabanı entity'si olduğunu belirtir
public class UserSession {
    
    /**
     * Session Benzersiz Kimliği (Primary Key)
     * 
     * Her session için otomatik artan unique ID.
     * Veritabanı seviyesinde session'ları ayırt etmek için kullanılır.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sessionId;

    /**
     * Session'ın Ait Olduğu Kullanıcı
     * 
     * @ManyToOne - Çoka-Bir ilişki (Bir kullanıcının birçok session'ı olabilir)
     * @JoinColumn - Foreign key sütun adını özelleştirme
     * 
     * Öğrenci Notu: Bir kullanıcı farklı cihazlardan aynı anda
     * giriş yapabilir, bu yüzden ManyToOne ilişki kullanılır.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Session Token (Oturum Anahtarı)
     * 
     * Kullanıcıyı tanımlamak için kullanılan benzersiz string.
     * Frontend'de cookie veya localStorage'da saklanır.
     * 
     * Güvenlik Notu: Token'ın tahmin edilmesi zor olmalı (UUID vs. kullanılmalı).
     */
    private String sessionToken;
    
    /**
     * Session Oluşturulma Zamanı
     * 
     * Session ne zaman başlatıldığını gösterir.
     * Log ve audit trail için önemlidir.
     */
    private LocalDateTime createdAt;
    
    /**
     * Session Bitiş Zamanı
     * 
     * Session'ın ne zaman süresinin dolacağını belirtir.
     * Bu tarihten sonra session geçersiz sayılır.
     * 
     * Öğrenci Notu: Güvenlik için session'ların süresiz olmaması önemlidir.
     * Genellikle 24 saat, 1 hafta gibi süreler kullanılır.
     */
    private LocalDateTime expiresAt;
    
    /**
     * Session Aktiflik Durumu
     * 
     * true: Session aktif
     * false: Session pasif/sonlandırılmış
     * 
     * Kullanıcı çıkış yapınca veya session süresi dolunca false yapılır.
     */
    private boolean isActive;
    
    /**
     * Kullanıcı Tarayıcı Bilgisi (User Agent)
     * 
     * Hangi browser ve işletim sisteminden bağlanıldığını gösterir.
     * Güvenlik analizi ve cihaz takibi için kullanılır.
     * 
     * Örnek: "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36"
     */
    private String userAgent;
    
    /**
     * Bağlantı IP Adresi
     * 
     * Kullanıcının hangi IP adresinden bağlandığını gösterir.
     * Güvenlik kontrolleri ve şüpheli aktivite tespiti için kullanılır.
     * 
     * Örnekler: "192.168.1.100", "203.45.67.89"
     */
    private String ipAddress;

    /**
     * Özel Constructor - Yeni Session Oluşturma
     * 
     * @param user Session'ın ait olduğu kullanıcı
     * @param sessionToken Benzersiz session anahtarı
     * @param expiresAt Session'ın bitiş zamanı
     * 
     * Öğrenci Notu: Bu constructor session oluştururken kullanılır.
     * CreatedAt otomatik olarak şu anki zaman atanır.
     * isActive varsayılan olarak true yapılır.
     */
    public UserSession(User user, String sessionToken, LocalDateTime expiresAt) {
        this.user = user;
        this.sessionToken = sessionToken;
        this.createdAt = LocalDateTime.now(); // Şu anki zaman
        this.expiresAt = expiresAt;
        this.isActive = true; // Yeni session aktif olarak başlar
    }
} 