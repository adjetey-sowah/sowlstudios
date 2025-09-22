package com.juls.sowlstudios.repository;



import com.juls.sowlstudios.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByOrderByCreatedAtDesc(Pageable pageable);

    Page<Booking> findByStatusOrderByCreatedAtDesc(Booking.BookingStatus status, Pageable pageable);

    List<Booking> findByStatus(Booking.BookingStatus status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startDate")
    Long countBookingsFromDate(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") Booking.BookingStatus status);

    @Query("SELECT b.packagePreference, COUNT(b) FROM Booking b GROUP BY b.packagePreference")
    List<Object[]> getPackageStatistics();

    @Query("SELECT b FROM Booking b WHERE b.graduationDate BETWEEN :startDate AND :endDate ORDER BY b.graduationDate")
    List<Booking> findByGraduationDateBetween(@Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :startOfDay AND b.createdAt < :endOfDay")
    Long countTodayBookings(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :weekStart")
    Long countWeeklyBookings(@Param("weekStart") LocalDateTime weekStart);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt >= :monthStart")
    Long countMonthlyBookings(@Param("monthStart") LocalDateTime monthStart);

    @Query("SELECT b FROM Booking b WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:startDate IS NULL OR b.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR b.createdAt <= :endDate) " +
            "ORDER BY b.createdAt DESC")
    Page<Booking> findBookingsWithFilters(@Param("status") Booking.BookingStatus status,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);

    List<Booking> findByCreatedAtBetweenAndStatus(LocalDateTime startDate, LocalDateTime endDate, Booking.BookingStatus status);

    List<Booking> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}