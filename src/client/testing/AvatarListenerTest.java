package client.testing;

/**
 * Manual Testing:
 * Testing for the AvatarListener class will be done manually. To do so involves
 * creating a ChatClient GUI and making sure that following effects occur during
 * the LoginWindow:
 *    - Hovering over an avatar changes the background color from DARK_BLUE
 *      to LIGHT_BLUE
 *    - Leaving the avatar changes to color of the username back to DARK_BLUE
 *    - If the background was white, hovering should have no effect
 *    - Clicking on the avatar results in the background turning white, as well
 *      as the background of the previously white avatar turning back to DARK_BLUE
 *    - Pressing on an avatar (without releasing) has no effect
 *    - Releasing over an avatar (without having pressed there) has no effect
 *    - At any given time, exactly one avatar has a white background
 *    - At any given time, at most one avatar has a LIGHT_BLUE background
 *    
 * @category no_didit
 */
public class AvatarListenerTest {

}
