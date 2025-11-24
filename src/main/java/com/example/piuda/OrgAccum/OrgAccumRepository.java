package com.example.piuda.OrgAccum;

import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.OrgAccum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgAccumRepository extends JpaRepository<OrgAccum, Long> {
    Optional<OrgAccum> findByOrg(Org org);
}
