package com.egebilmuh.medicaltracking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Medikal Takip Sistemi Ana Uygulama Sınıfı
 * 
 * Bu sınıf Spring Boot uygulamasının başlangıç noktasıdır.
 * @SpringBootApplication anotasyonu 3 önemli anotasyonu birleştirir:
 * 1. @Configuration - Bu sınıfın yapılandırma sınıfı olduğunu belirtir
 * 2. @EnableAutoConfiguration - Spring Boot'un otomatik yapılandırmasını etkinleştirir
 * 3. @ComponentScan - Mevcut paketten başlayarak component'leri tarar
 * 
 * Öğrenci Notu: Spring Boot uygulamasında mutlaka bir main class olmalıdır ve
 * @SpringBootApplication ile işaretlenmelidir. Bu, uygulamanın entry point'idir.
 */
@SpringBootApplication
public class MedicalTrackingApplication {

    /**
     * Uygulamanın ana başlangıç metodu
     * 
     * @param args Komut satırından gelen argümanlar
     * 
     * Öğrenci Notu: SpringApplication.run() metodu Spring Boot uygulamasını başlatır.
     * Bu metod şunları yapar:
     * - ApplicationContext'i oluşturur
     * - Bean'leri tarar ve kayıt eder  
     * - Embedded server'ı (Tomcat) başlatır
     * - Auto-configuration'ları çalıştırır
     */
    public static void main(String[] args) {
        SpringApplication.run(MedicalTrackingApplication.class, args);
    }

}
