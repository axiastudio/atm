package com.axiastudio.suite.plugins.atm.ws;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.axiastudio.suite.plugins.atm.wsa.getatto.GetAttoServiceStub;
import com.axiastudio.suite.plugins.atm.wsa.putatto.PutAttoServiceStub;

import com.axiastudio.suite.plugins.atm.utils.Utils;

public class ATMClient {

	private Logger log = Logger.getLogger(ATMClient.class);

	public static final String WSAKEY = "wsakey";
	public static final String USER_ID = "userID";
	public static final String MAC_NAME = "MAC";
	public static final String PASSWORD = "password";
	public static final String ENDPOINT = "endpoint";

	protected String wsakey;
	protected String userID;
	protected String MAC;
	protected String password;
	protected String endpoint;

	public ATMClient() {
	}

	public void bindService() {
		Locale.setDefault(Locale.UK);
	}

	public boolean checkResponseError(String response) {
		boolean ok = false;

		if (Utils.isWSError(response)) {
			// System.out.println("Is error: " + Utils.printWSError(response));
			log.error("[JSON] " + Utils.getErrorMessage(response));
			return true;
		}

		return ok;
	}

	public URL getEndpoint() throws MalformedURLException {
		return new URL(endpoint);
	}

	public String getCode() {
		StringBuffer code = new StringBuffer();
		code.append(wsakey).append(userID).append(MAC)
				.append(Utils.digest(password).toUpperCase());
		// System.out.println("Code: " + Utils.digest(code.toString()));
		return Utils.digest(code.toString());
	}

	public GetAttoServiceStub.GetToken getTokenGetRequest() {
		GetAttoServiceStub.GetToken t = new GetAttoServiceStub.GetToken();
		t.setCode(getCode());
		t.setUserid(userID);
		return t;
	}

	public PutAttoServiceStub.GetToken getTokenPutRequest() {
        PutAttoServiceStub.GetToken t = new PutAttoServiceStub.GetToken();
		t.setCode(getCode());
		t.setUserid(userID);
		return t;
	}
}
