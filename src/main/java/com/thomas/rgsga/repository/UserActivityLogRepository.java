package com.thomas.rgsga.repository;

import com.thomas.rgsga.entity.UserActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {

    @Query("SELECT a.activityType, COUNT(a) FROM UserActivityLog a GROUP BY a.activityType ORDER BY COUNT(a) DESC")
    List<Object[]> countByActivityType();

    Long countByActivityType(String activityType);

    Long countByCreatedAtAfter(LocalDateTime since);

    @Query("SELECT a FROM UserActivityLog a WHERE " +
           "(:query IS NULL OR LOWER(a.userEmail) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(a.details) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:activityType IS NULL OR a.activityType = :activityType)")
    Page<UserActivityLog> searchActivityLogs(@Param("query") String query,
                                             @Param("activityType") String activityType,
                                             Pageable pageable);

    List<UserActivityLog> findTop10ByOrderByCreatedAtDesc();

    List<UserActivityLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end);
}
