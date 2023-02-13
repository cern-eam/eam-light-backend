package ch.cern.cmms.eamlightejb.tools;

import org.apache.cxf.configuration.jsse.TLSClientParameters;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class Tools {

    public static String getVariableValue(String variableName) {
        String valueFromEnv = System.getenv().get(variableName);
        if (valueFromEnv != null && !valueFromEnv.isEmpty()) {
            return valueFromEnv;
        } else {
            return null;
        }
    }

    public static Integer getVariableIntegerValue(String variableName) {
        try {
            String value = Tools.getVariableValue(variableName);
            return Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return null;
        }
    }

    public static TLSClientParameters tlsClientParameters() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };

        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        tlsClientParameters.setTrustManagers(trustAllCerts);
        return tlsClientParameters;
    }

}
