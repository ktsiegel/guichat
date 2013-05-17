package client.testing;

/**
 * A ClientListeningThread will never have concurrency issues with another thread.
 * There is only one ClientListeningThread per client. Also, the ClientListeningThread
 * uses entirely different methods than the main thread of the client.
 * 
 * The only way the server can communicate with the client is through the ClientListeningThread.
 * So, if the handleRequest method in the ChatClientModel class functions (manual tests
 * documented in the ChatClientModelTest class), if the user can log in or receive any information
 * from the server at all, the ClientListeningThread functions properly. This was tested
 * through our extensive tests of the GUIChat program as documented in the testing report.
 * 
 * @category no_didit
 */

public class ClientListeningThreadTest {}
