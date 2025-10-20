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
@Table(name = "maintenancerecord")
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private ServiceAppointment appointment;

    @Column(name = "technician_ids", length = 255)
    private String technicianIds;

    @Column(name = "vehicle_condition", columnDefinition = "text")
    private String vehicleCondition;

    @Column(name = "checklist", columnDefinition = "text")
    private String checklist;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Column(name = "remarks", columnDefinition = "text")
    private String remarks;

    @OneToMany(mappedBy = "record")
    private Set<PartUsage> partUsages = new LinkedHashSet<>();
}
