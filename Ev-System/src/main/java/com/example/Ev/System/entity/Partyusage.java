package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "partyusage")
@Getter
@Setter
public class Partyusage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private Maintenancerecord record;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;   // 🔹 link to the Part

    @Column(name = "quantity_used", nullable = false)
    private Integer quantityUsed;

    @Column(name = "unit_cost")
    private Double unitCost;
}
