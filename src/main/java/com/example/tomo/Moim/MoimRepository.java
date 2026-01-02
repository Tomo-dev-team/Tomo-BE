package com.example.tomo.Moim;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MoimRepository extends JpaRepository<Moim, Long> {
    // 모임 생성하기

    // 모임명으로 이미 만들어진 모임인지 확인하기
    Boolean existsByTitle(String moimName);
    Optional<Moim> findByTitle(String moimName);


    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Moim m WHERE m.id IN :moimIds")
    void deleteMoimsByIds(@Param("moimIds") List<Long> moimIds);

    @Query("SELECT m FROM Moim m WHERE m.isPublic = true")
    List<Moim> findPublicMoims();

}
