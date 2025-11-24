package com.example.piuda.Report;

import com.example.piuda.domain.Entity.Org;
import com.example.piuda.domain.Entity.Pin;
import com.example.piuda.domain.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByPin(Pin pin);
    List<Report> findByOrg(Org org);
    
    @Query("SELECT SUM(r.reportPeople) FROM Report r WHERE r.org = :org")
    Integer sumPeopleByOrg(@Param("org") Org org);
    
    @Query("SELECT SUM(r.trash.trashKg) FROM Report r WHERE r.org = :org")
    Double sumKgByOrg(@Param("org") Org org);
    
    @Query("SELECT SUM(r.trash.trashL) FROM Report r WHERE r.org = :org")
    Double sumLByOrg(@Param("org") Org org);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.org = :org")
    Integer countByOrg(@Param("org") Org org);
    
    @Query("SELECT SUM(r.trash.trashKg) FROM Report r")
    Double sumTotalKg();
    
    @Query("SELECT SUM(r.trash.trashL) FROM Report r")
    Double sumTotalL();
    
    @Query("SELECT COUNT(r) FROM Report r")
    Integer countTotal();
    
    @Query("SELECT COUNT(DISTINCT r.org) FROM Report r")
    Integer countDistinctOrgs();
}