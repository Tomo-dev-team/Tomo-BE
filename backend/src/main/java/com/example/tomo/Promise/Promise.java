package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.global.Embedded.Location;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
public class Promise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="promise_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="moim_id")
    private Moim moim;

    @Embedded
    private Location location;

    private String place;

    private LocalTime promiseTime;
    private LocalDate promiseDate;
    private String promiseName;

    public Promise() {}

    public Promise(String promiseName, String place,
                   LocalTime promiseTime, LocalDate promiseDate, Location location) {
        this.place = place;
        this.promiseName = promiseName;
        this.promiseDate = promiseDate;
        this.promiseTime = promiseTime;
        this.location = location;
    }

    public void setMoimBasedPromise(Moim moim){
        this.moim = moim;
    }

}
