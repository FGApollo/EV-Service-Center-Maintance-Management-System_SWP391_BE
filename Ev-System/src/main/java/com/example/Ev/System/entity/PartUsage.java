package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
//@Table(name = "partyusage")
@Table(name = "\"PartUsage\"")
public class PartUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord record;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(name = "quantity_used", nullable = false)
    private Integer quantityUsed;

    @Column(name = "unit_cost")
    private Double unitCost;

}