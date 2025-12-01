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
    List<Report> findAllByOrderByReportDateDesc();
    List<Report> findByOrgOrderByReportDateDesc(Org org);
    
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
    
    // 단체별 월별 수거량(kg) 집계 (1월~12월)
    @Query("SELECT COALESCE(SUM(r.trash.trashKg), 0.0) FROM Report r " +
           "WHERE r.org = :org " +
           "AND FUNCTION('YEAR', r.reportDate) = :year " +
           "AND FUNCTION('MONTH', r.reportDate) = :month")
    Double sumKgByOrgAndMonth(@Param("org") Org org, @Param("year") int year, @Param("month") int month);
    
    // 단체별 월별 후기 작성 횟수 집계 (1월~12월)
    @Query("SELECT COUNT(r) FROM Report r " +
           "WHERE r.org = :org " +
           "AND FUNCTION('YEAR', r.reportDate) = :year " +
           "AND FUNCTION('MONTH', r.reportDate) = :month")
    Long countByOrgAndMonth(@Param("org") Org org, @Param("year") int year, @Param("month") int month);
    
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
    
    // 전체 월별 수거량(kg) 집계 (1월~12월)
    @Query("SELECT COALESCE(SUM(r.trash.trashKg), 0.0) FROM Report r " +
           "WHERE FUNCTION('YEAR', r.reportDate) = :year " +
           "AND FUNCTION('MONTH', r.reportDate) = :month")
    Double sumTotalKgByMonth(@Param("year") int year, @Param("month") int month);
    
    // 전체 월별 후기 작성 횟수 집계 (1월~12월)
    @Query("SELECT COUNT(r) FROM Report r " +
           "WHERE FUNCTION('YEAR', r.reportDate) = :year " +
           "AND FUNCTION('MONTH', r.reportDate) = :month")
    Long countTotalByMonth(@Param("year") int year, @Param("month") int month);
    
    // 개인 대시보드용: 유저가 작성한 후기의 report_id 목록
    @Query("SELECT r.reportId FROM Report r WHERE r.writer = :user")
    List<Long> findReportIdsByWriter(@Param("user") com.example.piuda.domain.Entity.User user);
    
    // 개인 대시보드용: 유저가 좋아요한 후기의 report_id 목록
    @Query("SELECT pa.report.reportId FROM PrivateActivity pa WHERE pa.user = :user")
    List<Long> findReportIdsByLikedUser(@Param("user") com.example.piuda.domain.Entity.User user);
    
    // 개인 대시보드용: report_id 목록에 해당하는 월별 후기 개수 (중복 제거)
    @Query("SELECT COUNT(DISTINCT r) FROM Report r " +
           "WHERE r.reportId IN :reportIds " +
           "AND FUNCTION('YEAR', r.reportDate) = :year " +
           "AND FUNCTION('MONTH', r.reportDate) = :month")
    Long countDistinctByReportIdsAndMonth(@Param("reportIds") List<Long> reportIds, 
                                          @Param("year") int year, 
                                          @Param("month") int month);
}