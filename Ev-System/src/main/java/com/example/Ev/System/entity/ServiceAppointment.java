package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "serviceappointment")
public class ServiceAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_center_id", nullable = false)
    private ServiceCenter serviceCenter;

    @Column(name = "appointment_date", nullable = false)
    private Instant appointmentDate;

    @ColumnDefault("'pending'")
    @Column(name = "status", length = 20)
    private String status;

    @ColumnDefault("now()")
    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToMany
    @JoinTable(
            name = "appointmentservice", // tên bảng trung gian
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "service_type_id")
    )
    private Set<ServiceType> serviceTypes = new LinkedHashSet<>();


    @OneToMany(mappedBy = "appointment")
    private Set<Invoice> invoices = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointment")
    private Set<MaintenanceRecord> maintenanceRecords = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointment")
    private Set<StaffAssignment> staffAssignments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "appointment")
    private Set<Worklog> worklogs = new LinkedHashSet<>();

}