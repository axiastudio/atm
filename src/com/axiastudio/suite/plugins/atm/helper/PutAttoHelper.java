package com.axiastudio.suite.plugins.atm.helper;

import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.axiastudio.suite.plugins.atm.AllegatoATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;
import com.axiastudio.suite.plugins.atm.ws.PutAttoClient;

public class PutAttoHelper {

	/*
	 * private static final String[] attoAttributes = new String[] { "dataatto",
	 * "numeroatto", "datapubblicazioneatto", "datascadenzaatto", "durataatto",
	 * "titoloatto", "oggettoatto", "tipoatto", "fileatto", "statoatto",
	 * "dataannullamentoatto", "motivoannullamentoatto", "responsabileatto",
	 * "progressivoatto", "datarevocaatto", "peraltroenteatto", "altroenteatto",
	 * "enteatto", "entedescatto", "annoatto", "numeroallegatiatto" };
	 */
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

	public boolean putAtto(PubblicazioneATM pubblicazione,
			List<AllegatoATM> files) {

		boolean toReturn = false;

		Map<String, Object> atto = fillAttoFromPubblicazione(pubblicazione,
				files);

		try {

			toReturn = getPutAttoClientInstance().putAtto(atto);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return toReturn;

	}

	public boolean putAtto(PubblicazioneATM pubblicazione) {

		return putAtto(pubblicazione, null);

	}

	private Map<String, Object> fillAttoFromPubblicazione(
			PubblicazioneATM pubblicazione, List<AllegatoATM> files) {

		Map<String, Object> atto = new HashMap<String, Object>();

		atto.put("d_dataatto", pubblicazione.getDataatto());

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
			String fileExtension = a.getFileallegatoname().substring(
					a.getFileallegatoname().indexOf('.'));

			try {

				StringBuffer buffer = new StringBuffer();
				DataInputStream dis = new DataInputStream(a.getFileallegato());
				int c = -1;
				while ((c = dis.read()) != -1) {
					buffer.append((char) c);
				}
				dis.close();

				// Base64 with second params if less than 4 no newlines
				Base64 encoder = new Base64();
				marshaledFile.append("{\"s_titoloallegato\":\"")
						.append(a.getTitoloallegato())
						.append("\",\"s_estensioneallegato\":\"")
						.append(fileExtension)
						.append("\",\"f_fileallegato\":\"")
						.append(new String(encoder.encode(buffer.toString().getBytes())))
						.append("\"}");

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
		
		System.out.println("JSON File allegati:\n|" + marshaledFile.toString()+"|");

		return marshaledFile.toString();

	}

}
