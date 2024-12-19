package org.example.pr3;

import io.reactivex.Observable;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class Task3 {
    public static class UserFriend {
        int userId;
        int friendId;

        public UserFriend(int userId, int friendId) {
            this.userId = userId;
            this.friendId = friendId;
        }

        @Override
        public String toString() {
            return "UserFriend{" +
                    "userId=" + userId +
                    ", friendId=" + friendId +
                    '}';
        }
    }

    public class UserFriendGenerator {
        private static final int MAX_USERS = 1000;
        private static final int MAX_FRIENDS = 100;

        public static UserFriend[] generateRandomUserFriends(int size) {
            Random random = new Random();
            UserFriend[] userFriends = new UserFriend[size];

            for (int i = 0; i < size; i++) {
                int userId = random.nextInt(MAX_USERS) + 1;
                int friendId = random.nextInt(MAX_USERS) + 1;
                userFriends[i] = new UserFriend(userId, friendId);
            }

            return userFriends;
        }
    }

    public static class UserFriendService {
        private UserFriend[] userFriends;

        public UserFriendService(UserFriend[] userFriends) {
            this.userFriends = userFriends;
        }

        public Observable<UserFriend> getFriends(int userId) {
            return Observable.fromArray(userFriends)
                    .filter(userFriend -> userFriend.userId == userId);
        }
    }

    public static void main(String[] args) {
        UserFriend[] userFriends = UserFriendGenerator.generateRandomUserFriends(1000);
        UserFriendService userFriendService = new UserFriendService(userFriends);

        int[] userIds = generateRandomUserIds(10);

        Observable.fromIterable(Arrays.stream(userIds).boxed().collect(Collectors.toList())) // Преобразуем массив в поток Integer
                .flatMap(userId -> userFriendService.getFriends(userId)) // Получаем друзей для каждого userId
                .subscribe(userFriend -> System.out.println(userFriend));
    }


    private static int[] generateRandomUserIds(int size) {
        Random random = new Random();
        int[] userIds = new int[size];

        for (int i = 0; i < size; i++) {
            userIds[i] = random.nextInt(1000) + 1; // userId от 1 до 1000
        }

        return userIds;
    }
}
