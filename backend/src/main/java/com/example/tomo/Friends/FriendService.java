package com.example.tomo.Friends;

import com.example.tomo.Friends.dtos.ResponseFriendDetailDto;
import com.example.tomo.Moim_people.MoimPeopleRepository;
import com.example.tomo.Users.User;
import com.example.tomo.Users.UserRepository;
import com.example.tomo.Users.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final MoimPeopleRepository moimPeopleRepository;
    private final FriendShipPolicy friendShipPolicy;
    private final UserService userService;

    // 매일 자정마다 실행
    @Transactional
    @Scheduled(cron = "0 * * * * *")
    public void updateAllFriendshipScores() {
        List<Friend> friends = friendRepository.findAll();

        for (Friend friend : friends) {
            long joinCount = moimPeopleRepository.countCommonMoims(
                    friend.getUser().getId(),
                    friend.getFriend().getId()
            );

            int score = friendShipPolicy.calculateTotalScore(
                    friend.getCreated_at(),
                    (int) joinCount
            );

            friend.updateFriendship(score);
        }

        // 일괄 저장
        friendRepository.saveAll(friends);
    }

    @Transactional
    public void removeFriend(String uid, String friendEmail) {

        User me = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new FriendException(FriendErrorCode.UNAUTHORIZED_USER));

        User other = userRepository.findByEmail(friendEmail)
                .orElseThrow(() -> new FriendException(FriendErrorCode.USER_NOT_FOUND));

        Friend relation = friendRepository.findByUserAndFriend(me, other)
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_RELATION_NOT_FOUND));

        friendRepository.delete(relation);

        // 반대 방향도 있으면 같이 삭제
        friendRepository.findByUserAndFriend(other, me)
                .ifPresent(friendRepository::delete);
    }

    @Transactional(readOnly = true)
    public ResponseFriendDetailDto getFriendDetail(String uid, String query) {

        User user = userService.getUser(query);
        Friend friend = getFriendByUidAndEmail(uid, user.getEmail());

        return new ResponseFriendDetailDto(
                user.getEmail(),
                user.getUsername(),
                friend.getFriendship(),
                friend.getCreated_at()
        );
    }


    @Transactional(readOnly = true)
    public List<ResponseFriendDetailDto> getFriends(String uid) {

        User user = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new FriendException(FriendErrorCode.UNAUTHORIZED_USER));

        return friendRepository.findAllByUserId(user.getId()).stream()
                .map(friend -> {
                    User f = friend.getFriend();
                    return new ResponseFriendDetailDto(
                            f.getEmail(),
                            f.getUsername(),
                            friend.getFriendship(),
                            friend.getCreated_at()
                    );
                })
                .toList();
    }



    @Transactional(readOnly = true)
    public Friend getFriendByUidAndEmail(String uid, String email) {

        User me = userRepository.findByFirebaseId(uid)
                .orElseThrow(() -> new FriendException(FriendErrorCode.UNAUTHORIZED_USER));

        User other = userRepository.findByEmail(email)
                .orElseThrow(() -> new FriendException(FriendErrorCode.USER_NOT_FOUND));

        return friendRepository.findByUserIdAndFriendId(me.getId(), other.getId())
                .orElseThrow(() -> new FriendException(FriendErrorCode.FRIEND_RELATION_NOT_FOUND));
    }




}
