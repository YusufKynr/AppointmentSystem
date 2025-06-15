package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.model.UserSession;
import com.egebilmuh.medicaltracking.service.SessionService;
import com.egebilmuh.medicaltracking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/session")
@CrossOrigin(origins = "http://localhost:3000")
public class SessionController {
    
    private final SessionService sessionService;
    private final UserService userService;
    
    public SessionController(SessionService sessionService, UserService userService) {
        this.sessionService = sessionService;
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData, 
                                                    HttpServletRequest request) {
        try {
            String email = loginData.get("email");
            String password = loginData.get("password");
            
            // Kullanıcı doğrulama
            User user = userService.login(email, password);
            
            // Session oluştur
            String userAgent = request.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(request);
            
            UserSession session = sessionService.createSession(user, userAgent, ipAddress);
            
            // Response hazırla
            Map<String, Object> response = new HashMap<>();
            response.put("sessionToken", session.getSessionToken());
            response.put("user", createUserResponse(user));
            response.put("expiresAt", session.getExpiresAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateSession(@RequestBody Map<String, String> sessionData) {
        try {
            String sessionToken = sessionData.get("sessionToken");
            
            Optional<UserSession> sessionOpt = sessionService.validateSession(sessionToken);
            
            if (sessionOpt.isPresent()) {
                UserSession session = sessionOpt.get();
                
                Map<String, Object> response = new HashMap<>();
                response.put("valid", true);
                response.put("user", createUserResponse(session.getUser()));
                response.put("expiresAt", session.getExpiresAt());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("error", "Geçersiz veya süresi dolmuş session");
                
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("valid", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> sessionData) {
        try {
            String sessionToken = sessionData.get("sessionToken");
            sessionService.invalidateSession(sessionToken);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Başarıyla çıkış yapıldı");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshSession(@RequestBody Map<String, String> sessionData) {
        try {
            String sessionToken = sessionData.get("sessionToken");
            UserSession refreshedSession = sessionService.refreshSession(sessionToken);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sessionToken", refreshedSession.getSessionToken());
            response.put("expiresAt", refreshedSession.getExpiresAt());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("userId", user.getUserId());
        userResponse.put("email", user.getEmail());
        userResponse.put("role", user.getRole());
        
        // Role'e göre ek bilgiler ekle
        if (user.getRole() == User.Role.DOCTOR) {
            // Doctor bilgilerini al
            try {
                var doctor = userService.getAllDoctors().stream()
                    .filter(d -> d.getUserId() == user.getUserId())
                    .findFirst();
                
                if (doctor.isPresent()) {
                    var d = doctor.get();
                    userResponse.put("name", d.getName() + " " + d.getSurname());
                    userResponse.put("specialty", d.getSpecialty());
                    userResponse.put("phoneNo", d.getPhoneNo());
                    userResponse.put("birthDate", d.getBirthDate());
                }
            } catch (Exception e) {
                userResponse.put("name", user.getEmail().split("@")[0]);
            }
        } else if (user.getRole() == User.Role.PATIENT) {
            // Patient bilgilerini al
            try {
                var patient = userService.getAllPatients().stream()
                    .filter(p -> p.getUserId() == user.getUserId())
                    .findFirst();
                
                if (patient.isPresent()) {
                    var p = patient.get();
                    userResponse.put("name", p.getName() + " " + p.getSurname());
                    userResponse.put("phoneNo", p.getPhoneNo());
                    userResponse.put("birthDate", p.getBirthDate());
                }
            } catch (Exception e) {
                userResponse.put("name", user.getEmail().split("@")[0]);
            }
        }
        
        return userResponse;
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
} 