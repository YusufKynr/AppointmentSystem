package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Doctor extends User {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String phoneNo;
    
    public enum Specialty {
        Dermatology,
        Cardiology,
        Eye,
        General_Surgery
    }
    
    @Enumerated(EnumType.STRING)
    private Specialty specialty;
    private boolean availability;
}
