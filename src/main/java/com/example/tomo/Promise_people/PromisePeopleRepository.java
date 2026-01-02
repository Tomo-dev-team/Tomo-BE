package com.example.tomo.Promise_people;

import com.example.tomo.Promise.Promise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface PromisePeopleRepository extends JpaRepository<Promise_people, Long> {

    @Query("""
    select pp.promise
    from Promise_people pp
    where pp.user.id = :userId
    """)
    List<Promise> findPromisesByUserId(Long userId);


    @Query("""
    select pp.promise
    from Promise_people pp
    where pp.user.id = :userId
      and (
            pp.promise.promiseDate > :today
         or (pp.promise.promiseDate = :today
             and pp.promise.promiseTime >= :nowTime)
      )
    order by pp.promise.promiseDate asc, pp.promise.promiseTime asc
    """)
    List<Promise> findUpcomingPromisesByUserId (Long userId,
        LocalDate today,
        LocalTime nowTime
);

}
