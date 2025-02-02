package com.example.CrudApplication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="OrdersBatches")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class OrdersBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(updatable = false, nullable = false)
    private String batchId;

    @Column(updatable = false, nullable = false)
    private String deliveryBoyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BatchStatus batchStatus;

    @Column()
    private LocalDateTime assignedAt;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

//    @OneToMany(mappedBy = "ordersBatch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<DeliveryOrder> deliveryOrders = new ArrayList<>();
}
