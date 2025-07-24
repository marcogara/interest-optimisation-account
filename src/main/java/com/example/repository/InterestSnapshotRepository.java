package com.example.repository;

import com.example.model.InterestSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestSnapshotRepository extends JpaRepository<InterestSnapshot, Long> {
    // Optional: InterestSnapshot findTopByOrderByValidFromDesc();
}
