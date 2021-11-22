package ch.cern.cmms.eamlightweb.tools;

import ch.cern.cmms.eamlightejb.tools.Tools;
import ch.cern.eam.wshub.core.tools.InforException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import javax.enterprise.context.ApplicationScoped;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@ApplicationScoped
public class OpenIdTools {

    /**
     * Verify the JWT Token and extract the user name
     *
     * @param authorizationHeader
     * @return
     * @throws InforException
     */
    public String getUserName(String authorizationHeader) throws InforException {

        try {
            Jws<Claims> jwt = Jwts.parserBuilder()
                    .setSigningKey(getPublicKey())
                    .build()
                    // Remove the 'Bearer ' from the Authorization header.
                    .parseClaimsJws(authorizationHeader.substring(7));
            String userName = (String) jwt.getBody().get(Tools.getVariableValue("OPENID_INFOR_USER_TOKEN"));
            return userName.toUpperCase();
        } catch (ExpiredJwtException expiredException) {
           throw new InforException("Expired.", null, null);
        }
    }

    private static PublicKey getPublicKey() {
        try {
            String rsaPublicKey = Tools.getVariableValue("OPENID_PUBLIC_KEY");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(keySpec);
        } catch (InvalidKeySpecException invalidKey) {
            System.out.println("Invalid key: " + invalidKey.getMessage());
        } catch (NoSuchAlgorithmException noSuchAlg) {
            System.out.println("No Such Alg: " + noSuchAlg.getMessage());
        }
        return null;

    }

}
