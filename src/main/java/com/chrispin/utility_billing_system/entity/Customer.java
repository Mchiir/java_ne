package rw.utility.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.utility.billing.enums.Status;

@Entity
@Table(name = "customers",
        uniqueConstraints = @UniqueConstraint(columnNames = "national_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_names", nullable = false)
    private String fullNames;

    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    @Column
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;

    /** Optional link to a login account (a ROLE_CUSTOMER user). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
