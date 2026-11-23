package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.VisitorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitorLogRepository extends JpaRepository<VisitorLog, Long> {

    @Query("SELECT COUNT(DISTINCT v.visitorId) FROM VisitorLog v")
    Long countDistinctVisitors();

    @Query("SELECT COUNT(DISTINCT v.visitorId) FROM VisitorLog v WHERE v.createdAt >= :since")
    Long countDistinctVisitorsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(v) FROM VisitorLog v WHERE v.createdAt >= :since")
    Long countPageViewsSince(@Param("since") LocalDateTime since);

    @Query("SELECT v.pageUrl, v.pageTitle, COUNT(v), COUNT(DISTINCT v.visitorId) FROM VisitorLog v GROUP BY v.pageUrl, v.pageTitle ORDER BY COUNT(v) DESC")
    List<Object[]> findTopPages();

    @Query("SELECT v.deviceType, COUNT(v) FROM VisitorLog v WHERE v.deviceType IS NOT NULL GROUP BY v.deviceType ORDER BY COUNT(v) DESC")
    List<Object[]> countByDeviceType();

    @Query("SELECT v.browser, COUNT(v) FROM VisitorLog v WHERE v.browser IS NOT NULL GROUP BY v.browser ORDER BY COUNT(v) DESC")
    List<Object[]> countByBrowser();

    @Query("SELECT v.os, COUNT(v) FROM VisitorLog v WHERE v.os IS NOT NULL GROUP BY v.os ORDER BY COUNT(v) DESC")
    List<Object[]> countByOs();

    @Query("SELECT v.country, COUNT(v) FROM VisitorLog v WHERE v.country IS NOT NULL AND v.country != '' GROUP BY v.country ORDER BY COUNT(v) DESC")
    List<Object[]> countByCountry();

    @Query("SELECT v FROM VisitorLog v WHERE (:query IS NULL OR LOWER(v.pageUrl) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(v.visitorId) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<VisitorLog> searchVisitorLogs(@Param("query") String query, Pageable pageable);

    List<VisitorLog> findByCreatedAtBetweenOrderByCreatedAtAsc(LocalDateTime start, LocalDateTime end);
}
