package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Nationalized;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "\"User\"")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer id;

    @Nationalized
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Nationalized
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Nationalized
    @Column(name = "phone", length = 20)
    private String phone;

    @Nationalized
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Nationalized
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @Nationalized
    @ColumnDefault("'active'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @ColumnDefault("sysdatetime()")
    @Column(name = "created_at")
    private Instant createdAt;

}