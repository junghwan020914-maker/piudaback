package com.example.piuda.Org;

import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgRepository extends JpaRepository<Org, Long> {
    Optional<Org> findByUser(User user);
}
