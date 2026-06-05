package rw.utility.billing.entity;

import jakarta.persistence.*;
import lombok.*;
import rw.utility.billing.enums.ERole;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 30)
    private ERole name;

    public Role(ERole name) {
        this.name = name;
    }
}
