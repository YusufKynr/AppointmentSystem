package com.egebilmuh.medicaltracking.repository;

import com.egebilmuh.medicaltracking.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Integer> {
    List<Doctor> findBySpecialty(Doctor.Specialty specialty);
    List<Doctor> findByAvailabilityTrue();
    List<Doctor> findBySpecialtyAndAvailabilityTrue(Doctor.Specialty specialty);
}
