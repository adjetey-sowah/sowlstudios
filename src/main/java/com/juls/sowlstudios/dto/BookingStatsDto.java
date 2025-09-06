package com.juls.sowlstudios.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatsDto {

    private Long totalBookings;
    private Long todayBookings;
    private Long weeklyBookings;
    private Long monthlyBookings;
    private Long pendingBookings;
    private Long confirmedBookings;
    private Long cancelledBookings;
    private Long completedBookings;
    private Map<String, Long> packageStats;
}
