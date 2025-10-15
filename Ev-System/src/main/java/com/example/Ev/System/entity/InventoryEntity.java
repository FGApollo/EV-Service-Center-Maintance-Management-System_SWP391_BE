package com.example.Ev.System.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "inventory")
@AllArgsConstructor
@NoArgsConstructor
public class InventoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name = "center_id")
    private ServiceCenter center;

    private Integer quantity;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    // Relationships
    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private PartEntity part;
}
