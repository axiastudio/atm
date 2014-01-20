package com.axiastudio.suite.plugins.atm.helper;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.axiastudio.suite.plugins.atm.AllegatoATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;
import com.axiastudio.suite.plugins.atm.ws.PutAttoClient;

import com.sun.org.apache.xml.internal.security.utils.Base64;

public class PutAttoHelper {

	private static final String[] attoAttributes = new String[] { "dataatto",
			"numeroatto", "datapubblicazioneatto", "datascadenzaatto",
			"durataatto", "titoloatto", "oggettoatto", "tipoatto", "fileatto",
			"statoatto", "dataannullamentoatto", "motivoannullamentoatto",
			"responsabileatto", "progressivoatto", "datarevocaatto",
			"peraltroenteatto", "altroenteatto", "enteatto", "entedescatto",
			"annoatto", "numeroallegatiatto" };

	private Map<String, String> context = null;
	private static PutAttoClient pac = null;

	public void setup(String userid, String password, String mac,
			String wsakey, String endpoint) {
		context = new HashMap<String, String>();
		context.put(ATMClient.USER_ID, userid);
		context.put(ATMClient.PASSWORD, password);
		context.put(ATMClient.MAC_NAME, mac);
		context.put(ATMClient.WSAKEY, wsakey);
		context.put(ATMClient.ENDPOINT, endpoint);
	}

	public PutAttoClient getPutAttoClientInstance() {
		if (pac == null) {
			if (context == null) {
				System.err.println("Please call setup first");
				return null;
			}
			pac = new PutAttoClient(context);
		}
		return pac;
	}

	public void putAtto(PubblicazioneATM pubblicazione, List<AllegatoATM> files) {

		Map<String, Object> atto = fillAttoFromPubblicazione(pubblicazione,
				files);

		try {

			getPutAttoClientInstance().putAtto(atto);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void putAtto(PubblicazioneATM pubblicazione) {

		putAtto(pubblicazione, null);

	}

	private Map<String, Object> fillAttoFromPubblicazione(
			PubblicazioneATM pubblicazione, List<AllegatoATM> files) {

		Map<String, Object> atto = new HashMap<String, Object>();

		atto.put("d_dataatto", pubblicazione.getInizioconsultazione());

		atto.put("n_durataatto", pubblicazione.getDurataconsultazione());

		atto.put("s_titoloatto", pubblicazione.getTitolo());

		atto.put("s_oggettoatto", pubblicazione.getDescrizione());

		atto.put("s_tipoatto", pubblicazione.getTipoatto());

		atto.put("s_altroenteatto", pubblicazione.getRichiedente());

		if (files != null) {
			atto.put("s_allegati", marshalingFiles(files));
		}

		return atto;
	}

	/**
	 * 
	 * @param files
	 * @return
	 */
	private String marshalingFiles(List<AllegatoATM> files) {
		StringBuffer marshaledFile = new StringBuffer();

		if (files.size() == 0) {
			return "{}";
		}

		marshaledFile.append("[");

		for (Iterator<AllegatoATM> i = files.iterator(); i.hasNext();) {
			AllegatoATM a = i.next();
			File f = a.getFileallegato();
			String fileExtension = f.getName().substring(
					f.getName().indexOf('.') + 1);

			try {

				byte[] buffer = new byte[(int) f.length()];
				DataInputStream dis = new DataInputStream(
						new FileInputStream(f));
				dis.read(buffer);
				dis.close();

				marshaledFile.append("{\"s_titoloallegato\":\"")
						.append(a.getTitoloallegato())
						.append("\",\"s_estensioneallegato\":\"")
						.append(fileExtension)
						.append("\",\"f_fileallegato\":\"")
						.append(Base64.encode(buffer)).append("\"}");

				if (i.hasNext()) {
					marshaledFile.append(",");
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		marshaledFile.append("]");

		System.out.println("JSON File allegati:\n" + marshaledFile.toString());

		return marshaledFile.toString();
	}

}
