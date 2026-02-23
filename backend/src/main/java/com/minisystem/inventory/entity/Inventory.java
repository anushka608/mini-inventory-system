package com.minisystem.inventory.entity;
import com.minisystem.inventory.enums.InventoryStatus;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="INVENTORY",
        uniqueConstraints=@UniqueConstraint(columnNames={"SKU","MRP","BATCH_NO"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="SKU", nullable=false)
    private String sku;

    @Column(name="MRP", nullable=false)
    private Double mrp;

    @Column(name="BATCH_NO", nullable=false)
    private String batchNo;

    @Column(name="QUANTITY", nullable=false)
    private Integer quantity;

    @Column(name="STATUS", nullable=false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus status;

    @Column(name="EXPIRY_DATE")
    private LocalDate expiryDate;

    @Column(name="CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name="UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate(){
        updatedAt = LocalDateTime.now();
    }
}