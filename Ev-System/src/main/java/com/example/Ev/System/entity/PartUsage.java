package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "partyusage")


public class PartUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @ManyToOne
    @JoinColumn(name = "record_id", nullable = false)
    private MaintenanceRecord record;

    @Column(name = "quantity_used", nullable = false)
    private Integer quantityUsed;

    @Column(name = "unit_cost", nullable = false)
    private Double unitCost;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;
}

