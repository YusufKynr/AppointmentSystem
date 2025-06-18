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
 */
@SpringBootApplication
public class MedicalTrackingApplication {

    /**
     * Uygulamanın ana başlangıç metodu
     * 
     * @param args Komut satırından gelen argümanlar
     *
     */
    public static void main(String[] args) {
        SpringApplication.run(MedicalTrackingApplication.class, args);
    }

}
