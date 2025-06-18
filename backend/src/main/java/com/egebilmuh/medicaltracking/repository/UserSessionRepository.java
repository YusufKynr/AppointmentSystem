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
 */
@Repository // Spring Data JPA component olarak işaretler
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {
    
    /**
     * Geçerli Session Token ile Session Bulma
     * 
     * @param sessionToken Doğrulanacak session token'ı
     * @return Optional<UserSession> - Aktif session bulunursa dolu, yoksa boş
     *
     */
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);
    
    /**
     * Kullanıcının Aktif Session'ını Bulma
     * 
     * @param user Session'ı aranacak kullanıcı
     * @return Optional<UserSession> - Kullanıcının aktif session'ı
     */
    Optional<UserSession> findByUserAndIsActiveTrue(User user);
    
    /**
     * Kullanıcının Tüm Session'larını Deaktive Etme
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user = :user")
    void deactivateAllUserSessions(User user);
    
    /**
     * Süresi Dolmuş Session'ları Temizleme
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.expiresAt < :now")
    void deactivateExpiredSessions(LocalDateTime now);
    
    /**
     * Belirli Session'ı Deaktive Etme (Logout)
     */
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.sessionToken = :sessionToken")
    void deactivateSession(String sessionToken);
} 