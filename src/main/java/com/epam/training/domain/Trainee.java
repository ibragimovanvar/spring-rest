package com.epam.training.domain;

import javax.persistence.*;

import com.epam.training.domain.enumeration.DomainStatus;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "trainees")
public class Trainee {

    @Id
    @Column(name = "user_id")
    private Long id;

    @MapsId
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Trainer> trainers;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Training> trainings;

    public Trainee(User user, LocalDate birthDate, String address) {
        this.user = user;
        this.birthDate = birthDate;
        this.address = address;
    }

    public Trainee(Long id) {
        this.id = id;
    }
}
