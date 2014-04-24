package com.axiastudio.suite.plugins.atm.ws;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.GetTokenResponse;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.PutAtto;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub.PutAttoResponse;

public class PutAttoClient extends ATMClient {

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

			System.out.println("\nToken: " + token);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void putAtto(Map attoMap) throws Exception {

		PutAtto atto = new PutAtto();

		try {
			atto.setApar(getAttoFromMap(attoMap));
			atto.setToken(token);

			PutAttoResponse response = srv.putAtto(atto);

			if (checkResponseError(response.get_return())) {
				System.out.println("Error: " + response.get_return());
				throw new Exception(response.get_return());
			} else {
				System.out.println("Atto sent: " + response.get_return());
			}

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		String sAltroEnteAtto = "";
//		String sAltroEnteAtto = (String) attoMap.get("s_altroenteatto");
//		if (sAltroEnteAtto != null && !"".equals(sAltroEnteAtto)) {
//			bAltroEnteAtto = true;
//		}

		json.append("{\"d_dataatto\":\"")
				.append(formatDate((Date) attoMap.get("d_dataatto")))
				.append("\",\"d_datapubblicazioneatto\":\"")
				.append(formatDate((Date) attoMap
						.get("d_datapubblicazioneatto")))
				.append("\",\"d_datascadenzaatto\":\"")
				.append(formatDate((Date) attoMap.get("d_datascadenzaatto")))
				.append("\",\"s_durataatto\":\"")
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
					.append((String) attoMap.get("f_fileatto"))
					.append("\"");
		}

		if (attoMap.containsKey("s_allegati")) {
			json.append(",\"s_allegati\": ").append(
					(String) attoMap.get("s_allegati"));
		}

		json.append("}");

		System.out.println("Serialized JSON:\n" + json.toString());

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
		return String.format("%1$td/%1$tm/%1$tY",
				Calendar.getInstance(Locale.ITALIAN));
		// return String.format("%1$tY/%1$tm/%1$td",
		// Calendar.getInstance(Locale.ITALIAN));
	}

}
