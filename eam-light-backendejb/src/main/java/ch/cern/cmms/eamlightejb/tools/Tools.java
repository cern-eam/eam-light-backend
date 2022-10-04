package ch.cern.cmms.eamlightejb.tools;

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

}
