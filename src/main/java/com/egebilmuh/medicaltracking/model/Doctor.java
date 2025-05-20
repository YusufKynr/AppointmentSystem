package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Doctor extends User {
    private String name;
    private String surname;
    private String address;
    private String phoneNo;
    private String email;
    private enum Specialty{
        Dermatology,
        Cardiology,
        Eye,
        General_Surgery
    };
    @Enumerated(EnumType.STRING)
    private Specialty speciality;
    private boolean availability;
}
