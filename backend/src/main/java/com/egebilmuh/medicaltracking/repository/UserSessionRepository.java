package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Kullanıcı Oturum Veri Erişim Katmanı (User Session Repository)
 * 
 * Bu interface UserSession entity'si için session management
 * işlemlerini gerçekleştiren özel sorgular içerir.
 * 
 * Önemli Özellikler:
 * - Aktif session doğrulama (token kontrolü)
 * - Kullanıcı bazlı session yönetimi
 * - Toplu session deaktivasyonu
 * - Süresi dolmuş session temizleme
 * 
 * Öğrenci Notu: Session management sistemin güvenliği için kritiktir.
 * Bu repository session'ların lifecycle'ını yönetir.
 */
@Repository // Spring Data JPA component olarak işaretler
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {
    
    /**
     * Geçerli Session Token ile Session Bulma
     * 
     * @param sessionToken Doğrulanacak session token'ı
     * @return Optional<UserSession> - Aktif session bulunursa dolu, yoksa boş
     * 
     * Öğrenci Notu: Method naming convention ile otomatik SQL:
     * SELECT * FROM user_session 
     * WHERE session_token = ? AND is_active = true
     * 
     * Kullanım: Kullanıcı isteklerinde authentication kontrolü.
     * Token geçerliyse ve aktifse kullanıcı giriş yapmış demektir.
     */
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);
    
    /**
     * Kullanıcının Aktif Session'ını Bulma
     * 
     * @param user Session'ı aranacak kullanıcı
     * @return Optional<UserSession> - Kullanıcının aktif session'ı
     * 
     * Öğrenci Notu: Bir kullanıcının aynı anda birden fazla aktif session'ı
     * olabileceği için bu metod sadece ilk bulunanı döndürür.
     * Multiple device support için findByUserAndIsActiveTrue() kullanılabilir.
     */
    Optional<UserSession> findByUserAndIsActiveTrue(User user);
    
    /**
     * Kullanıcının Tüm Session'larını Deaktive Etme
     * 
     * @param user Session'ları kapatılacak kullanıcı
     * 
     * @Modifying - Bu query'nin veritabanında değişiklik yapacağını belirtir
     * @Transactional - Transaction içinde çalışması gerektiğini belirtir
     * @Query - Custom JPQL sorgusu tanımlar
     * 
     * Öğrenci Notu: Custom JPQL kullanarak bulk update yaparız.
     * UPDATE user_session SET is_active = false WHERE user_id = ?
     * 
     * Kullanım: Güvenlik nedeniyle (şifre değişikliği, şüpheli aktivite)
     * kullanıcının tüm cihazlardan çıkış yapması gerektiğinde.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user = :user")
    void deactivateAllUserSessions(User user);
    
    /**
     * Süresi Dolmuş Session'ları Temizleme
     * 
     * @param now Şu anki zaman
     * 
     * Öğrenci Notu: Bu metod scheduled job olarak çalıştırılabilir.
     * Örneğin her gün gece süresi dolmuş session'ları temizler.
     * 
     * JPQL: UPDATE user_session SET is_active = false 
     *       WHERE expires_at < current_timestamp
     * 
     * Performans: Bu işlem database performansını etkileyebilir,
     * bu yüzden off-peak saatlerde çalıştırılmalı.
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.expiresAt < :now")
    void deactivateExpiredSessions(LocalDateTime now);
    
    /**
     * Belirli Session'ı Deaktive Etme (Logout)
     * 
     * @param sessionToken Kapatılacak session'ın token'ı
     * 
     * Kullanım: Kullanıcı logout butonuna bastığında çalışır.
     * Sadece ilgili session kapatılır, diğer cihazlar etkilenmez.
     * 
     * Güvenlik Notu: Session token'ı client tarafında da temizlenmelidir
     * (localStorage, cookie vs.)
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.sessionToken = :sessionToken")
    void deactivateSession(String sessionToken);
} 