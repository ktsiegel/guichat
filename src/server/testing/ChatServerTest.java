package server.testing;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
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
 * respond properly. In addition, we must make sure that the few public methods
 * in the ChatServer class are correct. To test the server's ability to process
 * messages, we use the dummy client to send messages to a server and make sure
 * its responses are right. We test each possible command.
 * 
 * To test thread-safety, we merely send a large number of commands to the
 * server and make sure that its final output is sensible.
 * 
 * All command tests will send a few invalid (badly formatted) commands. These
 * commands are ignored by the server. We send these commands at the beginning
 * of each test and verify that future results are not affected.
 * 
 * At the end, there is an integration test that tests everything combined.
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
     * Starts a server on port 5678.
     */
    @Before
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
    @Test(timeout = 2000)
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
     */
    public void logIn3Users(DummyClient client1, DummyClient client2,
            DummyClient client3) {
        client1.send("login_attempt Alex 1");
        assertEquals("login_success", client1.read());
        assertEquals("user_joins Alex 1", client1.read());

        client2.send("login_attempt Katie 2");
        assertEquals("login_success", client2.read());
        Set<String> client2Results = new HashSet<String>();
        client2Results.add(client2.read());
        client2Results.add(client2.read());
        assertTrue(client2Results.contains("user_joins Alex 1"));
        assertTrue(client2Results.contains("user_joins Katie 2"));
        assertEquals("user_joins Katie 2", client1.read());

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
    }

    /**
     * Makes sure that we can log a user out and that currently logged in users
     * are notified.
     */
    @Test
    public void logoutCommandTest() {
        // start clients
        DummyClient client1 = new DummyClient("localhost", 5678);
        DummyClient client2 = new DummyClient("localhost", 5678);
        DummyClient client3 = new DummyClient("localhost", 5678);

        // log in 3 users
        logIn3Users(client1, client2, client3);
        
        // log out

        client1.close();
        wait(100);
        client2.close();
        wait(100);
        client3.close();
    }

    @Test
    public void chatStartCommandTest() {

    }

    @Test
    public void groupChatStartCommandTest() {

    }

    @Test
    public void groupChatInviteCommandTest() {

    }

    @Test
    public void groupChatLeaveCommandTest() {

    }

    @Test
    public void sayCommandTest() {

    }

    @Test
    public void typingCommandTest() {

    }

    @Test
    public void clearedCommandTest() {

    }

    @Test
    public void concurrencyTest() {

    }

    @Test
    public void integrationTest() {

    }

    @Test
    public void forceLogoutTest() {

    }

    @Test
    public void addMessageToQueueTest() {

    }
}
