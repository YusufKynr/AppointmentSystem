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
     */
    private String name;
    
    /**
     * Hastanın Soyadı
     */
    private String surname;
    
    /**
     * Hastanın Doğum Tarihi
     */
    private LocalDate birthDate;
    
    /**
     * Hastanın Telefon Numarası
     */
    private String phoneNo;
}
