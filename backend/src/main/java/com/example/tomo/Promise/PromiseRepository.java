package com.example.tomo.Promise;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromiseRepository extends JpaRepository<Promise, Long> {

    boolean existsByPromiseName(String name);
    boolean existsByPromiseDateAndPromiseTime( LocalDate date,LocalTime time);

    List<Promise> findByMoimId(Long moimId);

    Optional<Promise> findByPromiseName(String name);
}
