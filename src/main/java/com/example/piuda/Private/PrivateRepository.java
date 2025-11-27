package com.example.piuda.Private;

import com.example.piuda.domain.Entity.Private;
import com.example.piuda.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrivateRepository extends JpaRepository<Private, Long> {
    Optional<Private> findByUser(User user);
}
