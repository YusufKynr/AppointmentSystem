package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Kullanıcı Varlık Sınıfı (Entity Class)
 * 
 * Bu sınıf veritabanındaki 'user' tablosunu temsil eder.
 * Hem Doctor hem de Patient sınıflarının ebeveyn (parent) sınıfıdır.
 *
 * Kullanılan Anotasyonlar:
 * @Entity - Bu sınıfın bir JPA entity olduğunu belirtir
 * @Inheritance - Kalıtım stratejisini belirler (JOINED = her sınıf için ayrı tablo)
 * Lombok anotasyonları - Boilerplate kodları otomatik oluşturur
 */
@Entity // JPA'ya bu sınıfın bir veritabanı entity'si olduğunu söyler
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor oluşturur (JPA için gerekli)
@AllArgsConstructor // Lombok: Tüm parametreleri alan constructor oluşturur
@Inheritance(strategy = InheritanceType.JOINED) // Kalıtım stratejisi: Her sınıf için ayrı tablo
public class User {
    
    /**
     * Birincil Anahtar (Primary Key)
     * 
     * @Id - Bu field'ın birincil anahtar olduğunu belirtir
     * @GeneratedValue - Değerin otomatik üretileceğini belirtir
     * IDENTITY stratejisi - Veritabanının auto-increment özelliğini kullanır
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    /**
     * Kullanıcı E-posta Adresi
     */
    private String email;
    
    /**
     * Kullanıcı Şifresi
     */
    private String password;
    
    /**
     * Kullanıcı Rolü Enum'u
     * 
     * Sistemde iki tür kullanıcı vardır:
     * - PATIENT: Hasta kullanıcıları
     * - DOCTOR: Doktor kullanıcıları
     */
    public enum Role {
        PATIENT, DOCTOR
    }
    
    /**
     * Kullanıcı Rolü Field'ı
     * @Enumerated(EnumType.STRING) - Enum değerlerini veritabanında string olarak saklar
     */
    @Enumerated(EnumType.STRING)
    private Role role;
}
