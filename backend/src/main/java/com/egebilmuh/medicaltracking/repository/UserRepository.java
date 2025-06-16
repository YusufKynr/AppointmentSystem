package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı Veri Erişim Katmanı (User Repository)
 * 
 * Bu interface Spring Data JPA kullanarak User entity'si için
 * veritabanı işlemlerini sağlar.
 * 
 * Öğrenci Notu: Repository Pattern kullanarak data access logic'i
 * business logic'ten ayırırız. Bu, kodun test edilebilirliğini
 * ve bakımını kolaylaştırır.
 * 
 * JpaRepository'den gelen hazır metodlar:
 * - save(), findAll(), findById(), delete(), count() vs.
 */
@Repository // Spring'e bu sınıfın Repository component'i olduğunu belirtir
public interface UserRepository extends JpaRepository<User, Integer> {
    
    /**
     * E-posta Adresine Göre Kullanıcı Bulma
     * 
     * @param username E-posta adresi (login için kullanılır)
     * @return Optional<User> - Kullanıcı bulunursa dolu, bulunamazsa boş Optional
     * 
     * Öğrenci Notu: Spring Data JPA method name convention kullanır.
     * "findUserByEmail" otomatik olarak şu SQL'e dönüşür:
     * SELECT * FROM user WHERE email = ?
     * 
     * Optional kullanımı null pointer exception'ları önler.
     */
    Optional<User> findUserByEmail(String username);

    /**
     * E-posta Adresinin Varlığını Kontrol Etme
     * 
     * @param username Kontrol edilecek e-posta adresi
     * @return boolean - E-posta mevcutsa true, değilse false
     * 
     * Öğrenci Notu: "existsByEmail" method name convention ile
     * otomatik olarak şu SQL'e dönüşür:
     * SELECT COUNT(*) > 0 FROM user WHERE email = ?
     * 
     * Kullanıcı kayıt işleminde duplicate email kontrolü için kullanılır.
     */
    boolean existsByEmail(String username);
}
