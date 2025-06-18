package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Doktor Varlık Sınıfı (Doctor Entity)
 * 
 * Bu sınıf User sınıfından miras alır ve doktorlara özel bilgileri içerir.
 * Veritabanında 'doctor' tablosunu oluşturur ve 'user' tablosu ile JOIN edilir.
 * 
 * Öğrenci Notu: Kalıtım (Inheritance) kullanarak kodun tekrarını önleriz.
 * Doctor sınıfı User'dan userId, email, password ve role field'larını miras alır.
 * 
 * JPA Inheritance Strategy: JOINED
 * - User bilgileri 'user' tablosunda saklanır
 * - Doctor'a özel bilgiler 'doctor' tablosunda saklanır
 * - İki tablo userId ile birleştirilir (JOIN)
 */
@Getter // Lombok: Tüm field'lar için getter metodları oluşturur
@Setter // Lombok: Tüm field'lar için setter metodları oluşturur
@NoArgsConstructor // Lombok: Parametresiz constructor (JPA zorunlu tutar)
@AllArgsConstructor // Lombok: Tüm field'ları parametre alan constructor
@Entity // JPA anotasyonu: Bu sınıfın veritabanı entity'si olduğunu belirtir
public class Doctor extends User {
    
    /**
     * Doktorun Adı
     */
    private String name;
    
    /**
     * Doktorun Soyadı
     */
    private String surname;
    
    /**
     * Doktorun Doğum Tarihi
     */
    private LocalDate birthDate;
    
    /**
     * Doktorun Telefon Numarası
     */
    private String phoneNo;
    
    /**
     * Doktor Uzmanlık Alanları Enum'u
     * 
     * Sistemde tanımlı uzmanlık alanları:
     * - Dermatology: Cildiye
     * - Cardiology: Kardiyoloji  
     * - Eye: Göz Hastalıkları
     * - General_Surgery: Genel Cerrahi
     */
    public enum Specialty {
        Dermatology,
        Cardiology,
        Eye,
        General_Surgery
    }
    
    /**
     * Doktorun Uzmanlık Alanı
     * 
     * @Enumerated(EnumType.STRING) - Enum değerini veritabanında string olarak saklar
     *
     */
    @Enumerated(EnumType.STRING)
    private Specialty specialty;
    
    /**
     * Doktorun Müsaitlik Durumu
     * 
     * true: Doktor randevu kabul ediyor
     * false: Doktor randevu kabul etmiyor (izinli, yoğun vs.)
     * 
     * boolean tipinde tutulur - sadece iki değer alabilir.
     */
    private boolean availability;
}
