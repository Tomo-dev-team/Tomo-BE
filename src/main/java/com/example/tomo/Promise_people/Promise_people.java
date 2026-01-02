package com.example.tomo.Promise_people;

import com.example.tomo.Promise.Promise;
import com.example.tomo.Users.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "promise_people")
@Getter
@NoArgsConstructor
public class Promise_people {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pp_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name ="promise_id")
    private Promise promise;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    private Boolean attendacne;

    public Promise_people(Promise promise, User user, Boolean attendacne) {
        this.promise = promise;
        this.user = user;
        this.attendacne = attendacne;

    }
}
