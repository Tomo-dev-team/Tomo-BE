package com.example.tomo.Promise;

import com.example.tomo.Moim.Moim;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Promise_people.PromisePeopleRepository;
import com.example.tomo.Promise_people.Promise_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromiseService {

    private final PromiseRepository promiseRepository;
    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final PromisePeopleRepository promisePeopleRepository;

    //약속 생성
    @Transactional
    public ResponsePostUniformDto addPromise(addPromiseRequestDTO dto){

        Moim moim = moimRepository.findByTitle(dto.getTitle())
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND)
                );

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

        promise.setMoimBasedPromise(moim);
        promiseRepository.save(promise);

        // 모임 내 약속으로 처리하기
        List<Promise_people> moimPeopleList = moim.getMoimPeopleList()
                .stream()
                .map(mp -> new Promise_people(promise, mp.getUser(), false))
                .collect(Collectors.toList());

        promisePeopleRepository.saveAll(moimPeopleList);


        return new ResponsePostUniformDto(
                true,
                promise.getPromiseName() + " 약속이 생성되었습니다"
        );
    }

    // 약속 단일 조회
    @Transactional(readOnly = true)
    public ResponseGetPromiseDto getPromise(String promiseName){

        Promise promise = promiseRepository.findByPromiseName(promiseName)
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.PROMISE_NOT_FOUND)
                );

        return new ResponseGetPromiseDto(
                promise.getPromiseName(),
                promise.getPromiseDate(),
                promise.getPromiseTime(),
                promise.getPlace()
        );
    }

    //모임의 모든 약속 조회
    @Transactional(readOnly = true)
    public List<ResponseGetPromiseDto> getAllPromise(String title){

        Moim moim = moimRepository.findByTitle(title)
                .orElseThrow(() ->
                        new PromiseException(PromiseErrorCode.MOIM_NOT_FOUND)
                );

        return promiseRepository.findByMoimId(moim.getId());
    }

    // 본인의 모든 약속 조회(달력 출력용)
    @Transactional(readOnly = true)
    public List<ResponseGetPromiseDto> getAllPromiseByUserId(String uid) {

        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return promisePeopleRepository.findPromisesByUserId(user.getId())
                .stream()
                .map(ResponseGetPromiseDto::from)
                .collect(Collectors.toList());
    }

    // 본인의 남은 약속 조회
    @Transactional(readOnly = true)
    public List<ResponseGetPromiseDto> getAllUpcomingPromiseByUserId(String userId) {

        User user = userRepository.findByFirebaseId(userId)
                .orElseThrow(()-> new UserException(UserErrorCode.USER_NOT_FOUND));

        return promisePeopleRepository.findUpcomingPromisesByUserId(user.getId(), LocalDate.now(), LocalTime.now())
                .stream()
                .map(ResponseGetPromiseDto::from)
                .collect(Collectors.toList());
    }



}
