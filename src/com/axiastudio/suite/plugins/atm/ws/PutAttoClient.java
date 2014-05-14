package com.axiastudio.suite.plugins.atm.ws;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.GetTokenResponse;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.PutAtto;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.PutAttoResponse;

public class PutAttoClient extends ATMClient {

	private Logger log = Logger.getLogger(PutAttoClient.class);
	
	private String endpoint = "";

	private PutAttoServiceStub srv = null;
	private String token = null;

	public PutAttoClient(Map<String, String> context) {

		wsakey = context.get(WSAKEY);
		userID = context.get(USER_ID);
		MAC = context.get(MAC_NAME);
		password = context.get(PASSWORD);

		endpoint = context.get(ENDPOINT);

		bindService();

	}

	@Override
	public void bindService() {
		Locale.setDefault(Locale.UK);

		try {

			srv = new PutAttoServiceStub(endpoint);

			// System.out.println("DATA: " + getCode() + ", " + userID);

			GetTokenResponse response = srv.getToken(getTokenPutRequest());

			// System.out.println("reponse text: " + jsonStrResponse);

			if (!checkResponseError(response.get_return())) {
				token = response.get_return();
			}

			log.debug("\nToken: " + token);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public boolean putAtto(Map attoMap) throws Exception {

		boolean toReturn = false;
		PutAtto atto = new PutAtto();

		try {
			atto.setApar(getAttoFromMap(attoMap));
			atto.setToken(token);

			PutAttoResponse response = srv.putAtto(atto);

			if (checkResponseError(response.get_return())) {
				log.debug("Error: " + response.get_return());
				throw new Exception(response.get_return());
			} else {
				log.debug("Atto sent: " + response.get_return());
			}

			toReturn = true;
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;
		
	}

	/**
	 * Serialize via JSON the Java Map.
	 * 
	 * @param attoMap
	 * @return
	 */
	private String getAttoFromMap(Map attoMap) {
		StringBuffer json = new StringBuffer();

		boolean bAltroEnteAtto = false;
		String sAltroEnteAtto = (String) attoMap.get("s_altroenteatto");
		if (sAltroEnteAtto != null && !"".equals(sAltroEnteAtto)) {
			bAltroEnteAtto = true;
		}

        json.append("{\"d_dataatto\":\"")
                .append(formatDate((Date) attoMap.get("d_dataatto")));

                if( attoMap.get("s_numeroatto") != null ) {
                    json.append("\",\"s_numeroatto\":\"")
                    .append(attoMap.get("s_numeroatto"));
                }

				json.append("\",\"s_durataatto\":\"")
				.append((Integer) attoMap.get("n_durataatto"))

				.append("\",\"s_titoloatto\":\"")
				.append((String) attoMap.get("s_titoloatto"))

				.append("\",\"s_oggettoatto\":\"")
				.append((String) attoMap.get("s_oggettoatto"))

				.append("\",\"s_tipoatto\":\"")
				.append((String) attoMap.get("s_tipoatto"))

				.append("\",\"s_statoatto\":\"")
				.append("pubblicazione")

				.append("\",\"b_peraltroenteatto\":\"").append(bAltroEnteAtto)

				.append("\"").append(",\"s_altroenteatto\":\"")
				.append(sAltroEnteAtto)

				.append("\",\"b_immediatoatto\":\"true\"");

		if (attoMap.containsKey("f_fileatto")) {
			json.append(",\"s_estensioneatto\":\"")
					.append((String) attoMap.get("s_estensioneatto"))
					.append("\",\"f_fileatto\":\"")
					.append((String) attoMap.get("f_fileatto")).append("\"");
		}

		if (attoMap.containsKey("s_allegati")
				&& !((String) attoMap.get("s_allegati")).equals("{}")) {
			json.append(",\"s_allegati\": ")
					.append((String) attoMap.get("s_allegati"))
					.append(",\"n_numallegatiatto\":\"")
					.append(attoMap.get("n_numallegatiatto")).append("\"");
		}

		json.append("}");
		
		log.debug("Serialized JSON:\n" + json.toString());

		return json.toString();
	}

	/**
	 * Format the date for the JSON:
	 * 
	 * yyyy/mm/dd
	 * 
	 * @param d
	 * @return
	 */
	private String formatDate(Date d) {
        Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
        calendar.setTime(d);
        String s = (new SimpleDateFormat("dd/MM/yyyy")).format(calendar.getTime());
        return s;
	}

}
