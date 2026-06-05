package rw.utility.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "meter_readings",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_reading_meter_month_year",
                columnNames = {"meter_id", "reading_month", "reading_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeterReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "meter_id", nullable = false)
    private Meter meter;

    @Column(name = "previous_reading", nullable = false, precision = 18, scale = 2)
    private BigDecimal previousReading;

    @Column(name = "current_reading", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentReading;

    /** currentReading - previousReading, computed at capture time. */
    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal consumption;

    @Column(name = "reading_date", nullable = false)
    private LocalDate readingDate;

    @Column(name = "reading_month", nullable = false)
    private Integer readingMonth;

    @Column(name = "reading_year", nullable = false)
    private Integer readingYear;
}
