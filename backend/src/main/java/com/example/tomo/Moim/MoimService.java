package com.example.tomo.Moim;


import com.example.tomo.Moim.dtos.MoimQueryResult;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Moim_people.Moim_people;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserErrorCode;
import com.example.tomo.Users.UserException;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.global.Embedded.Location;
import com.example.tomo.global.S3.S3UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MoimService {

    private final MoimRepository moimRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final S3UploadService s3UploadService;

    /* =====================
       모임 생성
       ===================== */
    @Transactional
    public Moim createMoim(
            String uid,
            String title,
            String description,
            Boolean isPublic,
            Location location,
            List<String> emails,
            MultipartFile image
    ) {

        User leader = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Moim moim = moimRepository.save(
                new Moim(title, description, isPublic, location)
        );

        // 1️⃣ 리더 등록
        moim.addMoimPeople(new Moim_people(moim, leader, true));

        // 2️⃣ 초대 사용자 등록
        for (String email : emails) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

            if (user.getId().equals(leader.getId())) {
                continue;
            }

            moim.addMoimPeople(new Moim_people(moim, user, false));
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = s3UploadService.uploadMoimImage(
                    moim.getId(),
                    image
            );
            moim.updateUrl(imageUrl);
        }

        return moim;
    }


    /* =====================
       모임 단일 조회
       ===================== */
    public MoimQueryResult getMoim(Long moimId, String uid) {

        Moim moim = findMoim(moimId);
        User user = findUser(uid);

        boolean isLeader = isLeader(moim.getId(), user.getId());
        List<String> emails = extractEmails(moim);

        return new MoimQueryResult(moim, emails, isLeader);
    }

    /* =====================
       내가 속한 모임 목록 조회
       ===================== */
    public List<MoimQueryResult> getMyMoims(String uid) {

        User user = findUser(uid);

        return moimPeopleRepository.findByUserId(user.getId())
                .stream()
                .map(mp -> {
                    Moim moim = mp.getMoim();
                    List<String> emails = extractEmails(moim);
                    boolean isLeader = mp.getLeader();
                    return new MoimQueryResult(moim, emails, isLeader);
                })
                .toList();
    }

    /* =====================
       공개 모임 조회
       ===================== */
    public List<MoimQueryResult> getPublicMoims() {

        return moimRepository.findPublicMoims()
                .stream()
                .map(moim -> new MoimQueryResult(
                        moim,
                        extractEmails(moim),
                        false
                ))
                .toList();
    }

    /* =====================
       모임 삭제
       ===================== */
    @Transactional
    public void deleteMoim(Long moimId, String uid) {

        User user = findUser(uid);
        Moim moim = findMoim(moimId);

        if (!isLeader(moim.getId(), user.getId())) {
            throw new MoimException(MoimErrorCode.NOT_MOIM_LEADER);
        }

        moimRepository.delete(moim);
    }

    /* =====================
       공통 내부 메서드
       ===================== */

    private Moim findMoim(Long moimId) {
        return moimRepository.findById(moimId)
                .orElseThrow(() -> new MoimException(MoimErrorCode.MOIM_NOT_FOUND));
    }

    private User findUser(String uid) {
        return userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private boolean isLeader(Long moimId, Long userId) {
        return moimPeopleRepository.findLeaderByMoimIdAndUserId(moimId, userId);
    }

    private List<String> extractEmails(Moim moim) {
        return moim.getMoimPeopleList()
                .stream()
                .map(mp -> mp.getUser().getEmail())
                .toList();
    }
}
