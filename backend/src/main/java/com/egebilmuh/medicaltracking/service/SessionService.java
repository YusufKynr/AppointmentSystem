package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.model.UserSession;
import com.egebilmuh.medicaltracking.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final UserSessionRepository sessionRepository;
    private static final int SESSION_DURATION_HOURS = 24; // 24 saat
    
    public UserSession createSession(User user, String userAgent, String ipAddress) {
        // Kullanıcının mevcut aktif sessionlarını deaktive et
        sessionRepository.deactivateAllUserSessions(user);
        
        // Yeni session token oluştur
        String sessionToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(SESSION_DURATION_HOURS);
        
        UserSession session = new UserSession(user, sessionToken, expiresAt);
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        
        return sessionRepository.save(session);
    }
    
    public Optional<UserSession> validateSession(String sessionToken) {
        // Expired sessionları temizle
        cleanupExpiredSessions();
        
        return sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken);
    }
    
    public void invalidateSession(String sessionToken) {
        sessionRepository.deactivateSession(sessionToken);
    }
    
    public void invalidateAllUserSessions(User user) {
        sessionRepository.deactivateAllUserSessions(user);
    }
    
    public UserSession refreshSession(String sessionToken) {
        Optional<UserSession> sessionOpt = sessionRepository.findBySessionTokenAndIsActiveTrue(sessionToken);
        
        if (sessionOpt.isPresent()) {
            UserSession session = sessionOpt.get();
            // Session süresini uzat
            session.setExpiresAt(LocalDateTime.now().plusHours(SESSION_DURATION_HOURS));
            return sessionRepository.save(session);
        }
        
        throw new RuntimeException("Geçersiz session token");
    }
    
    private void cleanupExpiredSessions() {
        sessionRepository.deactivateExpiredSessions(LocalDateTime.now());
    }
    
    public boolean isSessionValid(String sessionToken) {
        return validateSession(sessionToken).isPresent();
    }
} 