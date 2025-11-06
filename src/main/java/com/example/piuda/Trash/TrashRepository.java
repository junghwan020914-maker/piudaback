package com.example.piuda.Trash;

import com.example.piuda.domain.Entity.Trash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrashRepository extends JpaRepository<Trash, Long> {
}
