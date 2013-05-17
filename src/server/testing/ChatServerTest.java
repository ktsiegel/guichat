package server.testing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import server.ChatServer;

/**
 * To test the chat server, we set up dummy clients to send and read messages
 * from the server. The DummyClient class does nothing more than send messages
 * to the server and verify responses.
 * 
 * The general strategy is to test correctness and then thread-safety. If we
 * send a bunch of messages to the server at once, the server should be able to
 * process it all without any concurrency errors.
 * 
 * To test correctness, we just need to test its ability to process messages and
 * respond properly. To test the server's ability to process messages, we use
 * the dummy client to send messages to a server and make sure its responses are
 * right. We test each possible command. The few public methods in ChatServer
 * are mostly not testable (we can't access the connected sockets) but are used
 * in the ChatServer's functioning and must work for the other tests to
 * function. Thus, they are not tested separately here.
 * 
 * To test thread-safety, we merely send a large number of commands to the
 * server and make sure that its final output is sensible.
 * 
 * All command tests will send a few invalid (badly formatted) commands. These
 * commands are ignored by the server. We send these commands at the beginning
 * of each test and verify that future results are not affected.
 * 
 * @category no_didit
 */
public class ChatServerTest {
    /**
     * A helper method used to give the server time to send responses.
     * 
     * @param time
     *            A number of milliseconds to wait.
     */
    public void wait(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error in wait()");
        }
    }

    /**
     * Starts a server on port 5678. This is run before all the other tests and
     * makes sure that the server is startable.
     */
    @Test(timeout = 4000)
    public void startServer() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ChatServer server = new ChatServer(5678);
                try {
                    server.serve();
                } catch (Exception e) {
                    assertTrue(false);
                }
            }
        });
        thread.start();
    }

    /**
     * Tests the login_attempt command.
     * 
     * Makes sure that we get login_success if the username is valid and
     * login_invalid if the username is not valid.
     * 
     * This also makes sure that the server will notify all logged in users of
     * users that were already logged in and that original users are notified of
     * the logging in of a new user.
     */
    @Test(timeout = 4000)
    public void loginAttemptCommandTest() {
        // start clients
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // send invalid commands
        client1.send("login_attempt asdf asdf asdf asdfasdfasdf asdf asdf asdf adf");

        // login a valid user
        client1.send("login_attempt Alex 1");
        assertEquals("login_success", client1.read());
        assertEquals("user_joins Alex 1", client1.read());

        // login an invalid user
        client2.send("login_attempt Alex 2");
        assertEquals("login_invalid", client2.read());
        client2.send("login_attempt Katie 2");
        assertEquals("login_success", client2.read());
        // the server sends back users in an unknown order, so we have to check
        // them this way
        Set<String> client2Results = new HashSet<String>();
        client2Results.add(client2.read());
        client2Results.add(client2.read());
        assertTrue(client2Results.contains("user_joins Alex 1"));
        assertTrue(client2Results.contains("user_joins Katie 2"));
        assertEquals("user_joins Katie 2", client1.read());

        // login another user
        client3.send("login_attempt Casey 5");
        assertEquals("login_success", client3.read());
        Set<String> client3Results = new HashSet<String>();
        client3Results.add(client3.read());
        client3Results.add(client3.read());
        client3Results.add(client3.read());
        assertTrue(client3Results.contains("user_joins Alex 1"));
        assertTrue(client3Results.contains("user_joins Katie 2"));
        assertTrue(client3Results.contains("user_joins Casey 5"));
        assertEquals("user_joins Casey 5", client2.read());
        assertEquals("user_joins Casey 5", client1.read());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Code to log in three users on three dummy clients. The users are Alex,
     * Katie, and Casey. Also reads all messages related to these logins.
     * 
     * @param client1
     *            The client for Alex.
     * @param client2
     *            The client for Katie.
     * @param client3
     *            The client for Casey.
     * @param expectPrivateChats
     *            True if private chats were started among all the clients.
     */
    public void logIn3Users(DummyClient client1, DummyClient client2,
            DummyClient client3, boolean expectPrivateChats) {
        client1.send("login_attempt Alex 1");
        assertEquals("login_success", client1.read());
        assertEquals("user_joins Alex 1", client1.read());
        if (expectPrivateChats) {
            client1.read();
            client1.read();
        }

        client2.send("login_attempt Katie 2");
        assertEquals("login_success", client2.read());
        Set<String> client2Results = new HashSet<String>();
        client2Results.add(client2.read());
        client2Results.add(client2.read());
        assertTrue(client2Results.contains("user_joins Alex 1"));
        assertTrue(client2Results.contains("user_joins Katie 2"));
        assertEquals("user_joins Katie 2", client1.read());
        if (expectPrivateChats) {
            client2.read();
            client2.read();
        }

        client3.send("login_attempt Casey 5");
        assertEquals("login_success", client3.read());
        Set<String> client3Results = new HashSet<String>();
        client3Results.add(client3.read());
        client3Results.add(client3.read());
        client3Results.add(client3.read());
        assertTrue(client3Results.contains("user_joins Alex 1"));
        assertTrue(client3Results.contains("user_joins Katie 2"));
        assertTrue(client3Results.contains("user_joins Casey 5"));
        assertEquals("user_joins Casey 5", client2.read());
        assertEquals("user_joins Casey 5", client1.read());
        if (expectPrivateChats) {
            client3.read();
            client3.read();
        }
    }

    /**
     * Makes sure that we can log a user out and that currently logged in users
     * are notified.
     * 
     * This also makes sure that a user can invalidly log out by forcing a
     * connection closed, and current users are still notified.
     */
    @Test(timeout = 4000)
    public void logoutCommandTest() {
        // start clients
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, false);

        // invalid command
        client1.send("logout asdf asdf asdf asdf");

        // log out
        client1.send("logout Alex");

        // make sure other users are notified
        assertEquals("user_leaves Alex", client2.read());
        assertEquals("user_leaves Alex", client3.read());

        // forcefully close client 2
        client2.close();
        assertEquals("user_leaves Katie", client3.read());

        client3.close();
    }

    /**
     * Tests the ability to start a private chat with someone. Both users should
     * be notified.
     * 
     * Note that the chat_start command is ordered. We need to ensure that this
     * order is established.
     * 
     * Finally, if a user logs off while in a private conversation and then logs
     * back in, the private chat is immediately restarted.
     */
    @Test(timeout = 4000)
    public void chatStartCommandTest() {
        // start clients
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, false);

        // start a private chat between all 3 pairs of users
        client1.send("chat_start Alex Casey");
        wait(100);
        client2.send("chat_start Katie Alex");
        wait(100);
        client3.send("chat_start Casey Katie");

        // invalid command
        client1.send("chat_start Alex Casey Katie");

        // check responses
        assertEquals("chat_start 1 Alex Casey", client1.read());
        assertEquals("chat_start 1 Alex Casey", client3.read());
        assertEquals("chat_start 2 Katie Alex", client1.read());
        assertEquals("chat_start 2 Katie Alex", client2.read());
        assertEquals("chat_start 3 Casey Katie", client2.read());
        assertEquals("chat_start 3 Casey Katie", client3.read());

        // log a user out
        client1.send("logout Alex");
        assertEquals("user_leaves Alex", client2.read());
        assertEquals("user_leaves Alex", client3.read());

        // log Alex back in
        client1.send("login_attempt Alex 2");
        assertEquals("login_success", client1.read());
        // skip some messages
        client1.read();
        client1.read();
        client1.read();
        client2.read();
        client3.read();

        // make sure Alex is alerted of original private conversations
        assertEquals("chat_start 1 Casey Alex", client1.read());
        assertEquals("chat_start 2 Katie Alex", client1.read());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Tests to make sure that group chats can be started among any number of
     * users.
     * 
     * Makes sure that when a user leaves a group chat, all users in the group
     * chat are notified.
     */
    @Test(timeout = 4000)
    public void groupChatStartCommandTest() {
        // start clients
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, true);

        // invalid command
        client1.send("group_chat_start Alex Casey dinosaur Katie");

        // start a group chat
        client1.send("group_chat_start Alex Casey Katie");

        // verify results
        assertEquals("group_chat_start 4", client1.read());
        Set<String> client1Results = new HashSet<String>();
        client1Results.add(client1.read());
        client1Results.add(client1.read());
        assertTrue(client1Results.contains("group_chat_join 4 Casey"));
        assertTrue(client1Results.contains("group_chat_join 4 Katie"));
        assertEquals("group_chat_start 4", client2.read());
        Set<String> client2Results = new HashSet<String>();
        client2Results.add(client2.read());
        client2Results.add(client2.read());
        assertTrue(client2Results.contains("group_chat_join 4 Casey"));
        assertTrue(client2Results.contains("group_chat_join 4 Alex"));
        assertEquals("group_chat_start 4", client3.read());
        Set<String> client3Results = new HashSet<String>();
        client3Results.add(client3.read());
        client3Results.add(client3.read());
        assertTrue(client3Results.contains("group_chat_join 4 Alex"));
        assertTrue(client3Results.contains("group_chat_join 4 Katie"));

        // log a user out
        wait(100);
        client1.close();

        // check results
        assertEquals("group_chat_leave 4 Alex", client2.read());
        assertEquals("group_chat_leave 4 Alex", client3.read());

        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Makes sure the server correctly handles a user leaving a group
     * conversation.
     */
    @Test(timeout = 4000)
    public void groupChatLeaveCommandTest() {
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, true);

        // start a group chat
        client1.send("group_chat_start Alex Casey Katie");

        // skip messages
        client1.read();
        client1.read();
        client1.read();
        client2.read();
        client2.read();
        client2.read();
        client3.read();
        client3.read();
        client3.read();

        // invalid command
        client1.send("group_chat_leave 123 dinosaur");

        // have a user leave
        client1.send("group_chat_leave 5 Alex");

        // check results
        assertEquals("group_chat_leave 5 Alex", client2.read());
        assertEquals("group_chat_leave 5 Alex", client3.read());

        // have another user leave
        client2.send("group_chat_leave 5 Katie");

        // check results
        assertEquals("group_chat_leave 5 Katie", client3.read());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Makes sure the "say" command works in conversations.
     * 
     * Also makes sure that both empty messages and long messages with weird
     * characters (no newlines allowed) are good.
     */
    @Test(timeout = 4000)
    public void sayCommandTest() {
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, true);

        // start a group chat
        client1.send("group_chat_start Alex Casey Katie");

        // skip messages
        client1.read();
        client1.read();
        client1.read();
        client2.read();
        client2.read();
        client2.read();
        client3.read();
        client3.read();
        client3.read();

        // invalid command
        client1.send("say 123 dinosaur rabbit elephant");

        // say something
        client1.send("say 6 Alex"); // blank message
        assertEquals("say 6 Alex", client1.read().trim());
        assertEquals("say 6 Alex", client2.read().trim());
        assertEquals("say 6 Alex", client3.read().trim());
        client2.send("say 6 Katie hello"); // one word message
        assertEquals("say 6 Katie hello", client1.read().trim());
        assertEquals("say 6 Katie hello", client2.read().trim());
        assertEquals("say 6 Katie hello", client3.read().trim());
        client3.send("say 6 Casey hi im casey ho!@#(*&w is*&!(@) !!!);;; it going"); // more
                                                                                     // complex
                                                                                     // message
        assertEquals(
                "say 6 Casey hi im casey ho!@#(*&w is*&!(@) !!!);;; it going",
                client1.read().trim());
        assertEquals(
                "say 6 Casey hi im casey ho!@#(*&w is*&!(@) !!!);;; it going",
                client2.read().trim());
        assertEquals(
                "say 6 Casey hi im casey ho!@#(*&w is*&!(@) !!!);;; it going",
                client3.read().trim());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Tests to make sure the "typing" message is correctly propagated to all
     * users except for the user that was typing.
     */
    @Test(timeout = 4000)
    public void typingCommandTest() {
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, true);

        // start a group chat
        client1.send("group_chat_start Alex Casey Katie");

        // skip messages
        client1.read();
        client1.read();
        client1.read();
        client2.read();
        client2.read();
        client2.read();
        client3.read();
        client3.read();
        client3.read();

        // invalid command
        client1.send("typing asdf 123 dinosaur rabbit elephant");

        // say something
        client1.send("typing 7 Alex");
        assertEquals("typing 7 Alex", client2.read().trim());
        assertEquals("typing 7 Alex", client3.read().trim());
        client2.send("typing 7 Katie");
        assertEquals("typing 7 Katie", client1.read().trim());
        assertEquals("typing 7 Katie", client3.read().trim());
        client3.send("typing 7 Casey");
        assertEquals("typing 7 Casey", client1.read().trim());
        assertEquals("typing 7 Casey", client2.read().trim());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * Makes sure that the "cleared" command is correctly propagated to all
     * conversing users except the user from which the command was generated.
     */
    @Test(timeout = 4000)
    public void clearedCommandTest() {
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3, true);

        // start a group chat
        client1.send("group_chat_start Alex Casey Katie");

        // skip messages
        client1.read();
        client1.read();
        client1.read();
        client2.read();
        client2.read();
        client2.read();
        client3.read();
        client3.read();
        client3.read();

        // invalid command
        client1.send("cleared asdf 123 dinosaur rabbit elephant");

        // say something
        client1.send("cleared 8 Alex");
        assertEquals("cleared 8 Alex", client2.read().trim());
        assertEquals("cleared 8 Alex", client3.read().trim());
        client2.send("cleared 8 Katie");
        assertEquals("cleared 8 Katie", client1.read().trim());
        assertEquals("cleared 8 Katie", client3.read().trim());
        client3.send("cleared 8 Casey");
        assertEquals("cleared 8 Casey", client1.read().trim());
        assertEquals("cleared 8 Casey", client2.read().trim());

        wait(100);
        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    /**
     * This test logs in a large number of users and then logs out all the
     * users.
     * 
     * Then, we log in a single new user to make sure that there are no other
     * logged-in users.
     * 
     * This is a very basic test to make sure the server can handle a large load
     * and does not break with many operations in line.
     */
    @Test(timeout = 50000)
    public void concurrencyTest() {
        Thread[] threads = new Thread[1000];
        for (int i = 0; i < 1000; i++) {
            final int name = i;
            threads[i] = new Thread(new Runnable() {
                public void run() {
                    DummyClient client = new DummyClient("localhost", 5678);
                    client.send("login_attempt " + name + " 5");

                    client.send("logout " + name);

                    client.close();
                }
            });
        }

        for (int i = 0; i < 1000; i++) {
            threads[i].start();
        }

        for (int i = 0; i < 1000; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // make sure everyone is logged out
        DummyClient client = new DummyClient("localhost", 5678);
        client.send("login_attempt 1 5");
        assertEquals("login_success", client.read()); // that user must have
                                                      // been logged out
        assertEquals("user_joins 1 5", client.read());

        // there shouldn't be any other users online: to check this we will log
        // someone else in
        DummyClient client2 = new DummyClient("localhost", 5678);
        client2.send("login_attempt 555 2");
        assertEquals("login_success", client2.read());
        assertEquals("user_joins 555 2", client.read());
        // if no other users were online, user 1 receives this first

        wait(100);
        client.close();
        wait(100);
        client2.close();
    }
}
