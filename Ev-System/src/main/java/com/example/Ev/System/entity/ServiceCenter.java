package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "servicecenter")
@Where(clause = "status = 'active'")
public class ServiceCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "center_id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "center", cascade = CascadeType.ALL)
    private List<Inventory> inventories;

    @Column(name = "status", nullable = false, length = 20)
    @ColumnDefault("'active'")
    private String status;

    @OneToMany(mappedBy = "serviceCenter")
    private Set<ServiceAppointment> serviceAppointments = new LinkedHashSet<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = "active";
        }
    }
}