package server.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import server.CommunicationsData;

/**
 * ChatServerClientThread only has one method to test, the method that takes a
 * client connection and takes all messages and passes them onto the server.
 * 
 * This test uses a dummy chat server class that checks to make sure the
 * ChatServerClientThread successfully does two things:
 * 
 * First, all messages from the client must be passed onto the server.
 * 
 * Secondly, when a socket is closed forcefully (the client logs out without
 * pressing the log out button), the server must be notified.
 */
public class ChatServerClientThreadTest {
    /**
     * Sets up a dummy chat server and creates several clients to connect to it
     * and send messages.
     * 
     * This test makes sure that messages are passed to the server in the
     * correct order and that the server is notified when a client closes the
     * socket.
     * 
     * Note that no ChatServerClientThread is used directly in this test:
     * instead it is used in the ChatServer's serve() method.
     * 
     * @throws IOException
     *             for Socket errors.
     * @throws UnknownHostException
     *             for Socket errors.
     */
    @Test
    public void basicTest() throws UnknownHostException, IOException {
        DummyChatServer server = new DummyChatServer(5678);
        server.serve();

        // set up client connections
        Socket client1 = new Socket("localhost", 5678);
        Socket client2 = new Socket("localhost", 5678);
        PrintWriter out1 = new PrintWriter(client1.getOutputStream(), true);
        PrintWriter out2 = new PrintWriter(client2.getOutputStream(), true);

        // send some messages
        out1.println("test message 1");
        out2.println("test message 2");
        out1.println("test message 3");
        out1.println("another message !!@#*&^*&!^#@");
        out2.println("last message");

        // check the messages
        CommunicationsData first = server.nextData();
        Socket connected1 = first.getSocket(); // save this Socket for future
                                               // comparisons
        assertEquals("test message 1", first.getMessage());
        CommunicationsData second = server.nextData();
        Socket connected2 = second.getSocket(); // save this Socket for future
                                                // comparisons
        assertEquals("test message 2", second.getMessage());
        assertEquals(new CommunicationsData("test message 3", connected1),
                server.nextData());
        assertEquals(new CommunicationsData("another message !!@#*&^*&!^#@",
                connected1), server.nextData());
        assertEquals(new CommunicationsData("last message", connected2),
                server.nextData());
        
        // close the sockets
        client1.close();
        assertTrue(server.isLoggedOutSocket(connected1));
        assertTrue(!server.isLoggedOutSocket(connected1));
    }
}
