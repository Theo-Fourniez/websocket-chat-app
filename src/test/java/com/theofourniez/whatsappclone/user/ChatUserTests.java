package com.theofourniez.whatsappclone.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ChatUserTests {
    private ChatUser user;
    private ChatUser friend1;
    private ChatUser friend2;

    @BeforeEach
    public void setUp() {
        user = new ChatUser();
        user.setId(1L);
        user.setUsername("user");
        user.setPassword("password");

        friend1 = new ChatUser();
        friend1.setId(2L);
        friend1.setUsername("friend1");
        friend1.setPassword("password");

        friend2 = new ChatUser();
        friend2.setId(3L);
        friend2.setUsername("friend2");
        friend2.setPassword("password");
    }

    @Test
    public void testAddFriend_Success() {
        // Initially, the user's friend list should be empty
        Assertions.assertTrue(user.getFriends().isEmpty());
        // Add friend1 to the user's friend list
        user.addFriend(friend1);
        // Check if friend1 is added to the friend list
        Assertions.assertTrue(user.getFriends().contains(friend1));
    }

    @Test
    public void testAddFriend_DuplicateFriend() {
        // Initially, the user's friend list should be empty
        Assertions.assertTrue(user.getFriends().isEmpty());

        // Add friend1 to the user's friend list
        user.addFriend(friend1);

        // Add friend1 again, which should not be added as a duplicate
        user.addFriend(friend1);

        // Check if there is only one instance of friend1 in the friend list
        Assertions.assertEquals(1, user.getFriends().size());
    }

    @Test
    public void testAddFriend_Self() {
        // Initially, the user's friend list should be empty
        Assertions.assertTrue(user.getFriends().isEmpty());

        // Add the user itself as a friend
        user.addFriend(user);

        // Check if the user itself is not added to the friend list
        Assertions.assertTrue(user.getFriends().isEmpty());
    }

    @Test
    public void testRemoveFriend_Success() {
        // Initially, the user's friend list should be empty
        Assertions.assertTrue(user.getFriends().isEmpty());

        // Add friend1 and friend2 to the user's friend list
        user.addFriend(friend1);
        user.addFriend(friend2);

        // Check if friend1 and friend2 are added to the friend list
        Assertions.assertTrue(user.getFriends().contains(friend1));
        Assertions.assertTrue(user.getFriends().contains(friend2));

        // Remove friend1 from the user's friend list
        user.removeFriend(friend1);

        // Check if friend1 is removed from the friend list
        Assertions.assertFalse(user.getFriends().contains(friend1));
        // Check if friend2 is still in the friend list
        Assertions.assertTrue(user.getFriends().contains(friend2));
    }


}
