package com.axiastudio.suite.plugins.atm.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import com.axiastudio.suite.plugins.atm.wsa.getatto.GetAttoServiceStub.GetAttoInfo;

public class Utils {

	private static Logger log = Logger.getLogger(Utils.class.getName());
	
	public static String digest(String s){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte byteData[] = md.digest(s.getBytes());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            log.severe(ex.getMessage());
        }
        return null;
    }

	public static boolean isWSError(String wsResponse) {

		if ("".equals(wsResponse)) return false;
		
		// Avoid print unecessary error for non JSON string
		if (wsResponse.charAt(0) != '{') return false;
		
		try {
			
			JSONObject jsonResponse = new JSONObject(wsResponse);

			return (jsonResponse.has("messagetype") && 
					"error".equalsIgnoreCase(jsonResponse.getString("messagetype")));
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	public static String getErrorMessage(String wsResponse) {
		
		if ("".equals(wsResponse)) {
			return "";
		}
		
		if (wsResponse.charAt(0) == '{') return "";
		
		JSONObject jsonResponse;
		
		try {
			
			jsonResponse = new JSONObject(wsResponse);
			
//			TK00.* Token errato
//			AP01 Dati incompleti
//			AP02 Errore generico nei parametri passati al metodo
//			AP05.* Errori nei parametri passati al metodo
//			AP.06.* Errori negli allegati passati al metodo
//			AP.07.* Errori nelle operazioni di pubblicazione
			
			
			return jsonResponse.getString("code") + " " + jsonResponse.getString("message");
		
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
	}
	
	public static String printWSError(String wsError) {
		
		String error = "Response error:\n code: %s,\n description: %s\n",
				code = "",
				description = "";
		
		try {
			JSONObject jsonResponse = new JSONObject(wsError);
			
			code = jsonResponse.getString("code");
			description = jsonResponse.getString("description");
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return String.format(error, code, description);
	}

	public static GetAttoInfo buildGetAttoInfoFromJSON(JSONObject jsonObject) {
		GetAttoInfo gai = new GetAttoInfo();
		
		try {
			gai.setAttoid(jsonObject.getString("attoid"));
			gai.setInfo_req(jsonObject.getString("info_req"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return gai;
	}

	
}
