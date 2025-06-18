package com.egebilmuh.medicaltracking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security Güvenlik Konfigürasyon Sınıfı
 * 
 * Bu sınıf uygulamanın güvenlik ayarlarını yapılandırır.
 * @Configuration anotasyonu ile Spring'e bu sınıfın yapılandırma sınıfı olduğunu belirtiriz.
 */
@Configuration
public class SecurityConfig {

    /**
     * Şifre Encoder Bean Tanımı
     * 
     * @return BCryptPasswordEncoder - Güvenli şifre hashleme algoritması
     * 
     * @Bean anotasyonu ile Spring Container'a bu metodun bir bean döndürdüğünü belirtiriz.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Güvenlik Filter Chain Konfigürasyonu
     * 
     * @param httpSecurity HTTP güvenlik yapılandırma nesnesi
     * @return SecurityFilterChain - Yapılandırılmış güvenlik filter zinciri
     * @throws Exception Konfigürasyon hatası durumunda
     *
     * Güvenlik Notu: Gerçek projede authentication ve authorization eklenmelidir!
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                // CSRF (Cross-Site Request Forgery) korumasını devre dışı bırak
                // REST API'lerde genellikle kapatılır çünkü stateless'dir
                .csrf(AbstractHttpConfigurer::disable)
                // HTTP istekleri için yetkilendirme kuralları
                .authorizeHttpRequests(auth -> auth
                // Tüm isteklere izin ver (development amaçlı - production'da değiştirilmeli!)
                .anyRequest().permitAll());

        // Yapılandırılmış SecurityFilterChain'i döndür
        return httpSecurity.build();
    }
}
