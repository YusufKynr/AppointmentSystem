package com.egebilmuh.medicaltracking.controller;

import com.egebilmuh.medicaltracking.model.Doctor;
import com.egebilmuh.medicaltracking.model.Patient;
import com.egebilmuh.medicaltracking.model.User;
import com.egebilmuh.medicaltracking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:3000") // React frontend için CORS
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return userService.getUser(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/getAllUser")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/getAllDoctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        return ResponseEntity.ok(userService.getAllDoctors());
    }

    @GetMapping("/getAllPatients")
    public ResponseEntity<List<Patient>> getAllPatients() {
        return ResponseEntity.ok(userService.getAllPatients());
    }

    @GetMapping("/getDoctorsBySpecialty/{specialty}")
    public ResponseEntity<List<Doctor>> getDoctorsBySpecialty(@PathVariable String specialty) {
        try {
            Doctor.Specialty spec = Doctor.Specialty.valueOf(specialty);
            return ResponseEntity.ok(userService.getDoctorsBySpecialty(spec));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User updatedUser) {
        try {
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        try {
            User registeredUser = userService.register(user.getEmail(), user.getPassword(), user.getRole());
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/registerDoctor")
    public ResponseEntity<Doctor> registerDoctor(@RequestBody Map<String, Object> requestData) {
        try {
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            String name = (String) requestData.get("name");
            String surname = (String) requestData.get("surname");
            String birthDateStr = (String) requestData.get("birthDate");
            String phoneNo = (String) requestData.get("phoneNo");
            String specialtyStr = (String) requestData.get("specialty");
            
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            Doctor.Specialty specialty = Doctor.Specialty.valueOf(specialtyStr);
            
            Doctor doctor = userService.registerDoctor(email, password, name, surname, birthDate, phoneNo, specialty);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/registerPatient")
    public ResponseEntity<Patient> registerPatient(@RequestBody Map<String, Object> requestData) {
        try {
            String email = (String) requestData.get("email");
            String password = (String) requestData.get("password");
            String name = (String) requestData.get("name");
            String surname = (String) requestData.get("surname");
            String birthDateStr = (String) requestData.get("birthDate");
            String phoneNo = (String) requestData.get("phoneNo");
            
            LocalDate birthDate = LocalDate.parse(birthDateStr);
            
            Patient patient = userService.registerPatient(email, password, name, surname, birthDate, phoneNo);
            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) {
        try {
            User loggedInUser = userService.login(user.getEmail(), user.getPassword());
            return ResponseEntity.ok(loggedInUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok("Kullanıcı silindi");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
