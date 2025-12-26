package com.example.tomo.Moim;

import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.global.Embedded.Location;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name ="moim")
public class Moim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="moim_id")
    private Long id;


    // 모임 사람
    @OneToMany(mappedBy = "moim", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Moim_people> moimPeopleList = new ArrayList<>();

    private String title;

    private Location location;

    private Boolean isPublic;

    @Lob
    private String description;

    public Moim() {
    }

    public Moim(String title, String description, Boolean isPublic, Location location) {
        this.title = title;
        this.description = description;
        this.isPublic = isPublic;
        this.location = location;
    }

    private LocalDate createdAt;

    // DB에 저장해두고 가져와야함
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
    }
}
