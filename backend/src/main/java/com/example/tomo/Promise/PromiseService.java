package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Promise_people.PromisePeopleRepository;
import com.example.tomo.Promise_people.Promise_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.Users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final PromisePeopleRepository promisePeopleRepository;

    @Transactional
    public Promise addPromise(addPromiseRequestDTO dto) {

        Moim moim = moimRepository.findByTitle(dto.getTitle())
                .orElseThrow(() -> new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND));

        boolean duplicated =
                promiseRepository.existsByPromiseName(dto.getPromiseName()) &&
                        promiseRepository.existsByPromiseDateAndPromiseTime(
                                dto.getPromiseDate(),
                                dto.getPromiseTime()
                        );

        if (duplicated) {
            throw new PromiseException(PromiseErrorCode.PROMISE_ALREADY_EXISTS);
        }

        Promise promise = new Promise(
                dto.getPromiseName(),
                dto.getPlace(),
                dto.getPromiseTime(),
                dto.getPromiseDate(),
                dto.getLocation()
        );

        moim.addPromise(promise);
        promiseRepository.save(promise);

        // 🔥 모임 참가자 → 약속 참가자
        List<Promise_people> promisePeopleList =
                moim.getMoimPeopleList()
                        .stream()
                        .map(mp -> new Promise_people(promise, mp.getUser(), false))
                        .toList();

        promisePeopleRepository.saveAll(promisePeopleList);

        return promise; // ⭐ 여기 중요
    }


    @Transactional(readOnly = true)
    public PromiseQueryResult getPromise(String promiseName) {

        Promise promise = promiseRepository.findByPromiseName(promiseName)
                .orElseThrow(() -> new PromiseException(PromiseErrorCode.PROMISE_NOT_FOUND));

        Moim moim = promise.getMoim();

        return new PromiseQueryResult(
                promise,
                moim.getId(),
                moim.getTitle()
        );
    }


    @Transactional(readOnly = true)
    public List<PromiseQueryResult> getAllPromiseByUserId(String uid) {

        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return promisePeopleRepository.findPromisesByUserId(user.getId())
                .stream()
                .map(p -> new PromiseQueryResult(
                        p,
                        p.getMoim().getId(),
                        p.getMoim().getTitle()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PromiseQueryResult> getAllPromise(String moimTitle) {

        Moim moim = moimRepository.findByTitle(moimTitle)
                .orElseThrow(() -> new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND));


        return promiseRepository.findByMoimId(moim.getId())
                .stream()
                .map(promise -> new PromiseQueryResult(
                        promise,
                        moim.getId(),
                        moim.getTitle()
                ))
                .toList();
    }



    @Transactional(readOnly = true)
    public List<PromiseQueryResult> getAllUpcomingPromiseByUserId(String uid) {

        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return promisePeopleRepository
                .findUpcomingPromisesByUserId(
                        user.getId(),
                        LocalDate.now(),
                        LocalTime.now()
                )
                .stream()
                .map(p -> new PromiseQueryResult(
                        p,
                        p.getMoim().getId(),
                        p.getMoim().getTitle()
                ))
                .toList();
    }




}
