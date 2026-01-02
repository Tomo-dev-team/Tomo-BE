package com.example.tomo.Moim;

import com.example.tomo.Moim.dtos.*;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.dtos.userSimpleDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoimService {

    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;


    @Transactional // 이메일로 처리하기
    public addMoimResponseDto addMoim(String uid, addMoimRequestDto dto) {

        Moim moim = new Moim(dto.getTitle(), dto.getDescription(), dto.getIsPublic(), dto.getLocation()); // 일단 생성자도 변경해야 해서 그대로 두기
        Moim saved = moimRepository.save(moim);

        List<String> emailList = dto.getEmails();
        User leader = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new MoimException(MoimErrorCode.USER_NOT_FOUND));

        Moim_people moimLeader = new Moim_people(moim, leader, true);
        moimPeopleRepository.save(moimLeader);
        List<String> peopleList = new ArrayList<>();

        for (String email : emailList) {

            Optional<User> user = userRepository.findByEmail(email);

            if (user.isEmpty()) {
                throw new MoimException(MoimErrorCode.USER_NOT_FOUND);
            }

            peopleList.add(user.get().getUsername());

            Moim_people moim_people = new Moim_people(moim, user.get(), false);
            moimPeopleRepository.save(moim_people);
        }

        return new addMoimResponseDto(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription()
                ,peopleList);
    }

    // 모임 단일 조회
    @Transactional(readOnly = true)
    public getMoimResponseDto getMoim(Long moimId, String uid) {
        Moim moim = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MoimErrorCode.MOIM_NOT_FOUND));

        Long id = userRepository.findByFirebaseId(uid).orElseThrow(EntityNotFoundException::new).getId();
        // 모임의 리더 여부 출력
        Boolean moimLeader = moimPeopleRepository.findLeaderByMoimIdAndUserId(moim.getId(),id);

        return new getMoimResponseDto(
                moim.getId(),
                moim.getTitle(),
                moim.getDescription(),
                moim.getMoimPeopleList().size(),
                moimLeader,
                moim.getCreatedAt());

    }
    // 모임 상세 조회

    @Transactional(readOnly = true)
    public List<getMoimResponseDto> getMoimList(String userId){

        User user = userRepository.findByFirebaseId(userId)
                .orElseThrow(() -> new MoimException(MoimErrorCode.USER_NOT_FOUND));

        List<Moim_people> moims = moimPeopleRepository.findByUserId(user.getId());
        List<getMoimResponseDto> moimResponseDTOList = new ArrayList<>();

        for(Moim_people moim_people : moims){
            moimResponseDTOList.add(this.getMoim(moim_people.getMoim().getId(), userId));
        }

        return moimResponseDTOList;
    }

    @Transactional(readOnly = true)
    public getDetailMoimDto getMoimDetail(Long moimId){
        // 1. 모임명을 입력받아, 모임의 ID 알아내기 없다면 예외
        Moim find = moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MoimErrorCode.MOIM_NOT_FOUND));

        // 2. 모임 ID를 통해서 moim_people 테이블에서 모임 참가하는 사용자 ID 추출 없다면, 모임에 2명 이상 포함되어 있지 않습니다
        List<Long> userIdList = moimPeopleRepository.findUserIdsByMoimId(find.getId());
        // 3. .stream().map(entity :: toDto).collect.toList 로 반환하기
        List<userSimpleDto> userSimpleDtoList = new ArrayList<>();

        for (Long userId : userIdList) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new MoimException(MoimErrorCode.USER_NOT_FOUND));


            Boolean leader = moimPeopleRepository.findLeaderByMoimIdAndUserId(find.getId(), userId);
            userSimpleDto dto = new userSimpleDto(user.getEmail(),leader);
            userSimpleDtoList.add(dto);
        }

        return new getDetailMoimDto(
                find.getId(),
                find.getTitle(),
                find.getDescription(),
                userSimpleDtoList,
                find.getCreatedAt()
                );

    }
    @Transactional
    public void deleteMoim(Long moimId, String uid) {
        //1. 사용자가 리더일 때만 모임을 삭제할 수 있다.
        User user = userRepository.findByFirebaseId(uid).orElseThrow(EntityNotFoundException::new);
        Moim moim= moimRepository.findById(moimId).orElseThrow(EntityNotFoundException::new);
        if (!moimPeopleRepository.findLeaderByMoimIdAndUserId(moim.getId(), user.getId())) {
            throw new MoimException(MoimErrorCode.NOT_MOIM_LEADER);
        }
        //2. 삭제하려는 모임 가져오기,
        moimRepository.delete(moim);
    }

    @Transactional(readOnly = true)
    public List<getMyMoimDetailDto> getMyAndPublicMoim(String uid){
        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        return moimPeopleRepository.findMyMoimsByUserId(user.getId())
                .stream()
                .map(moim -> {

                    List<String> emails = moim.getMoimPeopleList()
                            .stream()
                            .map(mp -> mp.getUser().getEmail())
                            .collect(Collectors.toList());

                    return new getMyMoimDetailDto(
                            moim.getTitle(),
                            moim.getDescription(),
                            moim.getIsPublic(),
                            emails,
                            moim.getLocation()
                    );
                })
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<getMyMoimDetailDto> getPublicMoim() {

        List<getMyMoimDetailDto> moims = moimRepository.findPublicMoims()
                .stream()
                .map(moim ->
                {
                    List<String> emailList = moim.getMoimPeopleList()
                            .stream()
                            .map(mp -> mp.getUser().getEmail())
                            .collect(Collectors.toList());

                    return new getMyMoimDetailDto(
                            moim.getTitle(),
                            moim.getDescription(),
                            moim.getIsPublic(),
                            emailList,
                            moim.getLocation()
                    );
                }
                )
                .collect((Collectors.toList()));
        return moims;
    }

}

