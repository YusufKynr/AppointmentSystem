package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
