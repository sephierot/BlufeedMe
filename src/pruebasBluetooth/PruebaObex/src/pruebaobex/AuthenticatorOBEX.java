/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pruebaobex;

import javax.obex.Authenticator;
import javax.obex.PasswordAuthentication;

/**
 *
 * @author David
 */
public class AuthenticatorOBEX implements Authenticator{

    String nombreUsuario;

        public AuthenticatorOBEX(String nombreUsuario) {
                this.nombreUsuario = nombreUsuario;
        }

        public PasswordAuthentication onAuthenticationChallenge(String description, boolean isUserIdRequired, boolean isFullAccess) {
                System.out.println("challenge " + (isUserIdRequired?"U":"") + (isFullAccess?"A":"") + " " + description);
                return new PasswordAuthentication(nombreUsuario.getBytes(), (new String("0000")).getBytes());
        }

        public byte[] onAuthenticationResponse(byte[] userName) {
                System.out.println("authenticate " + new String(userName));
                return (new String("0000")).getBytes();
        }
}
