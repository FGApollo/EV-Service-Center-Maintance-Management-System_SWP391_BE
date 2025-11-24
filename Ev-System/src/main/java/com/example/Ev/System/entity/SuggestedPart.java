package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "suggested_part")
public class SuggestedPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "suggested_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false)
    private ServiceAppointment appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    private int quantity;

    @Column(name = "technician_note")
    private String technicianNote;

    private String status;

}
