package com.axiastudio.suite.plugins.atm.ws;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.axiastudio.suite.plugins.atm.wsa.getatto.GetAttoServiceStub;
import com.axiastudio.suite.plugins.atm.wsa.getatto.GetAttoServiceStub.GetAttoInfo;
import com.axiastudio.suite.plugins.atm.wsa.getatto.GetAttoServiceStub.GetAttoList;

import com.axiastudio.suite.plugins.atm.utils.Utils;

public class GetAttoClient extends ATMClient {

	// private String endpoint = "http://194.105.52.153/_wsa-t/getatto_v1.php";
	private String endpoint = "";
	
	private GetAttoServiceStub srv = null;
	private String token = null;

	public GetAttoClient(Map<String, String> context) {

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

			srv = new GetAttoServiceStub(endpoint);

			// System.out.println("DATA: " + getCode() + ", " + userID);

			GetAttoServiceStub.GetTokenResponse response = srv
					.getToken(getTokenGetRequest());

			// System.out.println("reponse text: " + jsonStrResponse);

			if (!checkResponseError(response.get_return())) {
				token = response.get_return();
			}

			System.out.println("\nToken: " + token);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<GetAttoServiceStub.GetAttoInfo> getAttoList(String filter) {

		List<GetAttoServiceStub.GetAttoInfo> response = new ArrayList<GetAttoServiceStub.GetAttoInfo>();

		if (token == null) {
			throw new Error("Service is not authenticated!");
		}

		if ("".equals(filter)) {
			filter = "{}";
		}

		try {
			GetAttoServiceStub.GetAttoListResponse attoList = srv
					.getAttoList(getAttoListRequest(filter));
			if (!checkResponseError(attoList.get_return())) {
				System.out.println("\nDeserialize atto list: "
						+ attoList.get_return());
			}
			JSONArray l = new JSONArray(attoList.get_return());
			for (int i = 0, len = l.length(); i < len; i++) {
				GetAttoInfo gai = Utils.buildGetAttoInfoFromJSON((JSONObject) l
						.get(i));
				System.out.println(l.get(i));
				response.add(gai);
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	private GetAttoList getAttoListRequest(String filter) {
		GetAttoServiceStub.GetAttoList gal = new GetAttoServiceStub.GetAttoList();
		gal.setFilter(filter);
		gal.setToken(token);
		return gal;
	}

}
