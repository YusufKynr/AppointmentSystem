package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Hasta Varlık Sınıfı (Patient Entity)
 * 
 * Bu sınıf User sınıfından miras alır ve hastalara özel bilgileri içerir.
 * Veritabanında 'patient' tablosunu oluşturur ve 'user' tablosu ile JOIN edilir.
 * 
 * Öğrenci Notu: Patient ve Doctor sınıfları benzer yapıdadır ancak farklı
 * iş mantıkları içerebilir. Patient'ta specialty field'ı yoktur, 
 * çünkü hastalar uzmanlık alanına sahip değildir.
 * 
 * Kalıtım Yapısı:
 * User (parent) -> Patient (child)
 * - User'dan gelen: userId, email, password, role
 * - Patient'a özel: name, surname, birthDate, phoneNo
 */
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor (JPA için zorunlu)
@AllArgsConstructor // Lombok: Tüm parametreleri alan constructor
@Entity // JPA anotasyonu: Bu sınıfın veritabanı entity'si olduğunu belirtir
public class Patient extends User {
    
    /**
     * Hastanın Adı
     * 
     * Kişisel bilgi olarak saklanır.
     * Randevu listelerinde ve hasta kayıtlarında görüntülenir.
     */
    private String name;
    
    /**
     * Hastanın Soyadı
     * 
     * Ad ile birlikte hastanın tam kimliğini oluşturur.
     * Veritabanında ayrı field olarak tutulur.
     */
    private String surname;
    
    /**
     * Hastanın Doğum Tarihi
     * 
     * LocalDate tipinde saklanır.
     * Yaş hesaplama ve kimlik doğrulama için kullanılabilir.
     * 
     * Öğrenci Notu: LocalDate immutable'dır (değiştirilemez).
     * Thread-safe'dir ve time zone problemleri yaşanmaz.
     * Format: YYYY-MM-DD (ISO 8601 standardı)
     */
    private LocalDate birthDate;
    
    /**
     * Hastanın Telefon Numarası
     * 
     * İletişim bilgisi olarak saklanır.
     * Randevu hatırlatmaları ve acil durumlar için kullanılır.
     * 
     * String tipinde tutulur çünkü:
     * - Matematiksel işlem yapılmaz
     * - Ülke kodları (+90) içerebilir
     * - Özel karakterler (-,(),space) içerebilir
     */
    private String phoneNo;
}
