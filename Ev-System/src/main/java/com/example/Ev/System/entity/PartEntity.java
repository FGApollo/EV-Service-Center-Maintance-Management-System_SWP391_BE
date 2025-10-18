package com.example.Ev.System.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "part")
@AllArgsConstructor
@NoArgsConstructor
public class PartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long partId;

    private String name;
    private String description;
    private Double unitPrice;

    @Column(name = "min_stock_level")
    private Integer minStockLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Relationships
    @OneToMany(mappedBy = "part")
    private List<InventoryEntity> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "part")
    private List<PartUsageEntity> partUsages = new ArrayList<>();
}
