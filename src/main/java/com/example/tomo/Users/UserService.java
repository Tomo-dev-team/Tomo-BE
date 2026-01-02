package com.example.tomo.Users;

import com.example.tomo.Friends.Friend;
import com.example.tomo.Friends.FriendErrorCode;
import com.example.tomo.Friends.FriendException;
import com.example.tomo.Friends.FriendRepository;
import com.example.tomo.Moim.MoimRepository;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.dtos.RequestUserSignDto;
import com.example.tomo.Users.dtos.ResponsePostUniformDto;
import com.example.tomo.Users.dtos.getFriendResponseDto;
import com.example.tomo.global.Exception.SelfFriendRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final MoimRepository moimRepository;

    /* =========================
       공통 조회 유틸
    ========================= */

    private User getUserByUid(String uid) {
        return userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new UserException(UserErrorCode.UNAUTHORIZED_USER));
    }

    /* =========================
       친구 관련
    ========================= */

    // 이미 친구 관계인지 여부
    public boolean alreadyFriend(User user, User friend) {
        return friendRepository.existsByUserAndFriend(user, friend);
    }

    @Transactional
    public ResponsePostUniformDto addFriends(String uid, String query) {

        User user = getUserByUid(uid);
        User friend = getUser(query);

        if (user.getEmail().equals(friend.getEmail())) {
            throw new SelfFriendRequestException("자기 자신은 친구로 추가할 수 없습니다.");
        }

        if (alreadyFriend(user, friend)) {
            throw new FriendException(FriendErrorCode.FRIEND_ALREADY_EXISTS);
        }

        Friend forward = new Friend(user, friend);
        Friend reverse = new Friend(friend, user);

        friendRepository.save(forward);
        friendRepository.save(reverse);

        return new ResponsePostUniformDto(true, "success");
    }

    public User getUser(String query) {

        User emailUser = userRepository.findByEmail(query).orElse(null);
        User inviteUser = userRepository.findByInviteCode(query).orElse(null);

        if (emailUser != null && inviteUser != null) {
            throw new UserException(UserErrorCode.INVALID_QUERY);
        }

        if (emailUser != null) return emailUser;
        if (inviteUser != null) return inviteUser;

        throw new UserException(UserErrorCode.USER_NOT_FOUND);
    }

    /* =========================
       회원가입
    ========================= */

    public boolean isUserAvailable(RequestUserSignDto dto) {
        return userRepository.findByFirebaseId(dto.getUuid()).isEmpty();
    }

    @Transactional
    public ResponsePostUniformDto signUser(RequestUserSignDto dto) {

        if (!isUserAvailable(dto)) {
            throw new UserException(UserErrorCode.USER_ALREADY_EXISTS);
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }

        User newUser = new User(
                dto.getUuid(),
                dto.getUsername(),
                dto.getEmail()
        );

        userRepository.save(newUser);
        return new ResponsePostUniformDto(true, "success");
    }


    @Transactional(readOnly = true)
    public getFriendResponseDto getUserInfo(String query) {
        User user = getUser(query);
        return new getFriendResponseDto(user.getUsername(), user.getEmail());
    }
    @Transactional
    public void saveRefreshToken(String uid, String refreshToken) {
        User user = getUserByUid(uid);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @Transactional
    public void logout(String uid) {
        User user = getUserByUid(uid);
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String uid) {

        User user = getUserByUid(uid);
        Long userId = user.getId();

        // 리더 모임 처리
        List<Long> leaderMoimIds = moimPeopleRepository.findLeaderMoimIds(userId);

        if (!leaderMoimIds.isEmpty()) {
            moimPeopleRepository.deleteMoimPeopleByMoimIds(leaderMoimIds);
            moimRepository.deleteMoimsByIds(leaderMoimIds);
        }

        // 일반 멤버 모임 탈퇴
        moimPeopleRepository.deleteUserFromNonLeaderMoims(userId);

        // 친구 관계 삭제
        friendRepository.deleteAllByUserId(userId);

        // 사용자 삭제
        userRepository.delete(user);
    }
}
