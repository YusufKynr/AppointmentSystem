package com.egebilmuh.medicaltracking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int appointmentId;

    @ManyToOne
    private Doctor doctor;
    @ManyToOne
    private Patient patient;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, CANCELLED
    };

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime appointmentDateTime;
}
