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
    
    // 단체별 쓰레기 세부 항목 집계
    @Query("SELECT COALESCE(SUM(r.trash.trashPet), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashPetByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashBag), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashBagByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashNet), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashNetByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashGlass), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashGlassByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashCan), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashCanByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashRope), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashRopeByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashCloth), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashClothByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashElec), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashElecByOrg(@Param("org") Org org);
    
    @Query("SELECT COALESCE(SUM(r.trash.trashEtc), 0) FROM Report r WHERE r.org = :org")
    Integer sumTrashEtcByOrg(@Param("org") Org org);
    
    @Query("SELECT SUM(r.trash.trashKg) FROM Report r")
    Double sumTotalKg();
    
    @Query("SELECT SUM(r.trash.trashL) FROM Report r")
    Double sumTotalL();
    
    @Query("SELECT COUNT(r) FROM Report r")
    Integer countTotal();
    
    @Query("SELECT COUNT(DISTINCT r.org) FROM Report r")
    Integer countDistinctOrgs();
    
    // 전체 쓰레기 세부 항목 집계
    @Query("SELECT COALESCE(SUM(r.trash.trashPet), 0) FROM Report r")
    Integer sumTotalTrashPet();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashBag), 0) FROM Report r")
    Integer sumTotalTrashBag();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashNet), 0) FROM Report r")
    Integer sumTotalTrashNet();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashGlass), 0) FROM Report r")
    Integer sumTotalTrashGlass();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashCan), 0) FROM Report r")
    Integer sumTotalTrashCan();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashRope), 0) FROM Report r")
    Integer sumTotalTrashRope();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashCloth), 0) FROM Report r")
    Integer sumTotalTrashCloth();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashElec), 0) FROM Report r")
    Integer sumTotalTrashElec();
    
    @Query("SELECT COALESCE(SUM(r.trash.trashEtc), 0) FROM Report r")
    Integer sumTotalTrashEtc();
}