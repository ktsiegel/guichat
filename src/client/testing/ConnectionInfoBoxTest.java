package client.testing;

/**
 * All testing for the ConnectionInfoBox will be done manually. The tests
 * will check that the ConnectionInfoBox is formatted and functioning properly.
 * The ConnectionInfoBox should be formatted as follows:
 * 
 *     --------------------
 *     | IPLabel          |
 *     --------------------
 *     | portLabel        |
 *     --------------------
 *     | submitButton     |
 *     --------------------
 *     
 * We will check for the following things: 
 *     - Overall Layout:
 *         - IPLabel, PortLabel, and submitButton are laid out as shown above
 *         - Window is not resizable
 *     - IPLabel:
 *         - Contains a JTextField with initial value "192.30.35.221"
 *         - JTextField is editable
 *         - Has left-aligned title "Input IP Address of Server Host"
 *     - portLabel:
 *         - Contains a JTextField with initial values "4567"
 *         - JTextField is editable
 *         - Has left-aligned title "Input Port Number"
 *     - submitButton:
 *         - Contains a JButton with label "Start Chat Client!"
 *         - Clicking button closes the window and opens a ChatClient with specified
 *           port number and IP address
 */
public class ConnectionInfoBoxTest {

}
