package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Kullanıcı Veri Erişim Katmanı (User Repository)
 */
@Repository // Spring'e bu sınıfın Repository component'i olduğunu belirtir
public interface UserRepository extends JpaRepository<User, Integer> {
    

    Optional<User> findUserByEmail(String username);

    boolean existsByEmail(String username);
}
