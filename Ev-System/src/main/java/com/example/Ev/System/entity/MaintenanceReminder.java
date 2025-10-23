package com.example.Ev.System.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "maintenance_reminder")
public class MaintenanceReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reminder_id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull
    @ColumnDefault("now()")
    @Column(name = "reminder_date", nullable = false)
    private Instant reminderDate;

    @Column(name = "message", length = Integer.MAX_VALUE)
    private String message;

    @ColumnDefault("false")
    @Column(name = "is_sent")
    private Boolean isSent;

    @Column(name = "sent_at")
    private Instant sentAt;

}