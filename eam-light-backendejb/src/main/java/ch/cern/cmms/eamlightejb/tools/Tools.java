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

    public static Map<String, String> createCodeDescOrgMap(String code, String desc, String organization) {
        Map<String, String> result = createCodeDescMap(code, desc);
        result.put("organization", organization);
        return result;
    }

    public static Map<String, String> createCodeDescMap(String code, String desc) {
        Map<String, String> result = new HashMap<>();
        result.put("code", code);
        result.put("desc", desc);
        return result;
    }

}
