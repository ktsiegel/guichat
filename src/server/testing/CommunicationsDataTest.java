package server.testing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Before;
import org.junit.Test;

import server.CommunicationsData;

/**
 * Tests the CommunicationsData class. The class is immutable and only has two
 * accessors, so there is not much to test other than the constructor and the
 * two accessors.
 * 
 * The tests will focus on accuracy.
 * 
 * @category no_didit
 */
public class CommunicationsDataTest {
    /**
     * Starts a server on port 5678 that client sockets can connect to.
     */
    @Before
    public void startServer() {
        try {
            final ServerSocket server = new ServerSocket(5678);
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        try {
                            server.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Test failed due to socket error");
        }
    }

    /**
     * Tests the CommunicationsData with a few types of inputs, including an
     * empty message and a null socket.
     */
    @Test
    public void accuracyTest() {
        CommunicationsData data1 = new CommunicationsData("", null);
        assertEquals("", data1.getMessage());
        assertEquals(null, data1.getSocket());

        try {
            Socket socket = new Socket("localhost", 5678);
            CommunicationsData data2 = new CommunicationsData("hello", socket);
            assertEquals("hello", data2.getMessage());
            assertEquals(socket, data2.getSocket());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("Test failed due to socket error");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Test failed due to socket error");
        }
    }
}
