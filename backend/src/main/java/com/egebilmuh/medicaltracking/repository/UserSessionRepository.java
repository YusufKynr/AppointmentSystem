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

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {
    
    Optional<UserSession> findBySessionTokenAndIsActiveTrue(String sessionToken);
    
    Optional<UserSession> findByUserAndIsActiveTrue(User user);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.user = :user")
    void deactivateAllUserSessions(User user);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.expiresAt < :now")
    void deactivateExpiredSessions(LocalDateTime now);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserSession s SET s.isActive = false WHERE s.sessionToken = :sessionToken")
    void deactivateSession(String sessionToken);
} 