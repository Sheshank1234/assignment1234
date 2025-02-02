package com.example.CrudApplication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;


@Entity
@Table(name="DeliveryOrders")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class DeliveryOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(updatable = false, nullable = false)
    private String orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus orderStatus;

    @Column()
    private LocalDateTime assignedAt;

    @Column(nullable = false)
    private String restaurantName;

    @Column(nullable = false)
    private double restaurantLat;

    @Column(nullable = false)
    private double restaurantLong;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private double customerLat;

    @Column(nullable = false)
    private double customerLong;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

//    @ManyToOne
//    @JoinColumn(name = "orders_batch_id", nullable = false)
//    private OrdersBatch ordersBatch;

}
