package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
public class MaintenanceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "appointment_id", nullable = false)
    private ServiceAppointment appointment;

    @Nationalized
    @Lob
    @Column(name = "vehicle_condition")
    private String vehicleCondition;

    @Nationalized
    @Lob
    @Column(name = "checklist")
    private String checklist;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @Nationalized
    @Lob
    @Column(name = "remarks")
    private String remarks;

}