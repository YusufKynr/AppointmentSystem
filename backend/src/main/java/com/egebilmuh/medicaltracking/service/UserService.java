package com.egebilmuh.medicaltracking.service;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.repository.DoctorRepository;
import com.egebilmuh.medicaltracking.repository.PatientRepository;
import com.egebilmuh.medicaltracking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> getUser(int userId) {
        return userRepository.findById(userId);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public List<Doctor> getDoctorsBySpecialty(Doctor.Specialty specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }

    public User updateUser(int id, User updatedUser) {
        User existUser = getUser(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: id=" + id));
        if (updatedUser.getPassword() != null) {
            existUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }
        if (updatedUser.getEmail() != null) {
            existUser.setEmail(updatedUser.getEmail());
        }

        return userRepository.save(existUser);
    }

    public User saveUser(User user) {
        if (userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("Kullanıcı zaten mevcut: id=" + user.getUserId());
        }
        return userRepository.save(user);
    }

    public void deleteUser(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("Kullanıcı bulunamadı: id=" + userId);
        }
        userRepository.deleteById(userId);
    }

    // User kayıt
    public User register(String email, String password, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        String hashedPassword = passwordEncoder.encode(password);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);
        newUser.setRole(role);

        return userRepository.save(newUser);
    }

    // Doctor kayıt
    public Doctor registerDoctor(String email, String password, String name, String surname, 
                                LocalDate birthDate, String phoneNo, Doctor.Specialty specialty) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        String hashedPassword = passwordEncoder.encode(password);

        Doctor doctor = new Doctor();
        doctor.setEmail(email);
        doctor.setPassword(hashedPassword);
        doctor.setRole(User.Role.DOCTOR);
        doctor.setName(name);
        doctor.setSurname(surname);
        doctor.setBirthDate(birthDate);
        doctor.setPhoneNo(phoneNo);
        doctor.setSpecialty(specialty);
        doctor.setAvailability(true);

        return doctorRepository.save(doctor);
    }

    // Patient kayıt
    public Patient registerPatient(String email, String password, String name, String surname,
                                  LocalDate birthDate, String phoneNo) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Kullanıcı zaten mevcut: email=" + email);
        }

        String hashedPassword = passwordEncoder.encode(password);

        Patient patient = new Patient();
        patient.setEmail(email);
        patient.setPassword(hashedPassword);
        patient.setRole(User.Role.PATIENT);
        patient.setName(name);
        patient.setSurname(surname);
        patient.setBirthDate(birthDate);
        patient.setPhoneNo(phoneNo);

        return patientRepository.save(patient);
    }

    public User login(String email, String password){
        User existUser = userRepository.findUserByEmail(email)
                .orElseThrow(()-> new RuntimeException("Kullanıcı bulunamadı"));
        
        if(!passwordEncoder.matches(password, existUser.getPassword())){
            throw new RuntimeException("Şifre yanlış");
        }
        return existUser;
    }
}