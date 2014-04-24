package com.axiastudio.suite.plugins.atm.helper;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.axiastudio.suite.plugins.atm.AllegatoATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;
import com.axiastudio.suite.plugins.atm.ws.PutAttoClient;

public class PutAttoHelper {

	private Logger log = Logger.getLogger(PutAttoHelper.class);
	
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

		if (pubblicazione.getFileAtto() != null) {
			AllegatoATM fa = pubblicazione.getFileAtto();

			atto.put(
					"s_estensioneatto",
					fa.getFileallegatoname().substring(
							fa.getFileallegatoname().indexOf('.') + 1));

			atto.put(
					"f_fileatto",
					marshalingFileAtto(fa));
		}
		
		if (files != null) {
			atto.put("s_allegati", marshalingFiles(files));
			atto.put("n_numallegatiatto", files.size());
		}

		return atto;
	}

	private String marshalingFileAtto(AllegatoATM fa) {

		try {

			Base64 encoder = new Base64();

			int len = (int) (new File(fa.getFileallegatoname())).length();

			return new String(encoder.encode(loadBytesFile(
					fa.getFileallegato(), len, fa.getFileallegatoname())));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
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
					a.getFileallegatoname().indexOf('.')+1);

			try {

				File f = new File(a.getFileallegatoname());

				// Base64 with second params if less than 4 no newlines
				Base64 encoder = new Base64();
				marshaledFile
						.append("{\"s_titoloallegato\":\"")
						.append(a.getTitoloallegato())
						.append("\",\"s_estensioneallegato\":\"")
						.append(fileExtension)
						.append("\",\"f_fileallegato\":\"")
						.append(new String(encoder.encode(loadBytesFile(
								a.getFileallegato(), (int) f.length(),
								a.getFileallegatoname())))).append("\"}");

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
		
		log.debug("JSON File allegati:\n|" + marshaledFile.toString()+"|");

		return marshaledFile.toString();

	}

	private static byte[] loadBytesFile(InputStream is, int len, String name)
			throws IOException {

		if (len > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[len];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + name);
		}

		is.close();
		return bytes;
	}
	
}
