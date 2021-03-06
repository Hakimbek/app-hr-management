package uz.pdp.apphemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Turniket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Boolean active;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp loginTime;

    @UpdateTimestamp
    private Timestamp logoutTime;
}
