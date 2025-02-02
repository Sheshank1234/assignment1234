package com.example.CrudApplication.repo;

import com.example.CrudApplication.model.OrdersBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersBatchRepository extends JpaRepository<OrdersBatch, Long> {
}
