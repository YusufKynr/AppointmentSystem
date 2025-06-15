package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.Entity;
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
public class Patient extends User {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String phoneNo;
}
