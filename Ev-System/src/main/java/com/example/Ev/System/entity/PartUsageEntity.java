package com.example.Ev.System.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "partyusage")
@AllArgsConstructor
@NoArgsConstructor
public class PartUsageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "quantity_used")
    private Integer quantityUsed;

    @Column(name = "unit_cost")
    private Double unitCost;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private PartEntity part;
}
