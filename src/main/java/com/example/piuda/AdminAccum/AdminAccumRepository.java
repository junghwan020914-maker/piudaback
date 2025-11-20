package com.example.piuda.AdminAccum;

import com.example.piuda.domain.Entity.Admin;
import com.example.piuda.domain.Entity.AdminAccum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminAccumRepository extends JpaRepository<AdminAccum, Long> {
    Optional<AdminAccum> findByAdmin(Admin admin);
}
