package com.example.piuda.Org;

import com.example.piuda.domain.Entity.Org;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgRepository extends JpaRepository<Org, Long> {
}
