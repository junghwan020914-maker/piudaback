package com.example.piuda.Pin;

import com.example.piuda.domain.Entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PinRepository extends JpaRepository<Pin, Long> {
    @Query("SELECT p FROM Pin p " +
            "LEFT JOIN Report r ON r.pin = p " +
            "LEFT JOIN Trash t ON r.trash = t " +
            "WHERE (:startDate IS NULL OR r.reportDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.reportDate <= :endDate) " +
            "AND (:region IS NULL OR p.region = :region) " +
            "AND (:organizationNames IS NULL OR EXISTS (" +
            "   SELECT 1 FROM Report r2 WHERE r2.pin = p " +
            "   AND r2.reportName IN :organizationNames " +
            "   AND (:startDate IS NULL OR r2.reportDate >= :startDate) " +
            "   AND (:endDate IS NULL OR r2.reportDate <= :endDate)" +
            ")) " +
            "GROUP BY p.pinId " +
            "HAVING (:minKg IS NULL OR SUM(t.trashKg) >= :minKg) " +
            "AND (:minL IS NULL OR SUM(t.trashL) >= :minL) " +
            "AND (:maxKg IS NULL OR SUM(t.trashKg) <= :maxKg) " +
            "AND (:maxL IS NULL OR SUM(t.trashL) <= :maxL)")
    List<Pin> findWithFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("organizationNames") List<String> organizationNames,
            @Param("region") Pin.Region region,
            @Param("minKg") Double minKg,
            @Param("minL") Double minL,
            @Param("maxKg") Double maxKg,
            @Param("maxL") Double maxL
    );

    @Query("SELECT p FROM Pin p " +
            "WHERE p.pinColor = :color " +
            "AND p.pinCreatedAt < :cutoff " +
            "AND (SELECT COUNT(r) FROM Report r WHERE r.pin = p) = 0 " +
            "AND (SELECT COUNT(rv) FROM Reserv rv WHERE rv.pin = p) = 0")
    List<Pin> findDeletablePins(
            @Param("color") Pin.PinColor color,
            @Param("cutoff") LocalDateTime cutoff
    );
}




