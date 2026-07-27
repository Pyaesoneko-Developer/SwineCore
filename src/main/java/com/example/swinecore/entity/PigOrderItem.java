package com.example.swinecore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pig_order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PigOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private PigOrder order;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pig_id", nullable = false)
    private Pig pig;

    private Double unitPrice;
}
