package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "MaintenanceRecord")
public class Maintenancerecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private ServiceAppointment appointment;

    // 🔹 Replace @ManyToOne User technician with a String
    @Column(name = "technician_ids", columnDefinition = "NVARCHAR(MAX)")
    private String technicianIds; // e.g. "2,5"

    @Column(name = "vehicle_condition")
    private String vehicleCondition;

    @Column(name = "checklist")
    private String checklist;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "remarks")
    private String remarks;

    // 🔹 Keep relationship to PartUsage
    @OneToMany(mappedBy = "record", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Partyusage> partyusages = new LinkedHashSet<>();
}
