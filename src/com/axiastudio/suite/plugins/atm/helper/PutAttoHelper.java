package com.axiastudio.suite.plugins.atm.helper;

import java.io.*;
import java.util.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.axiastudio.suite.plugins.atm.FileATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;
import com.axiastudio.suite.plugins.atm.ws.PutAttoClient;

public class PutAttoHelper {

	private Logger log = Logger.getLogger(PutAttoHelper.class);
	
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

	public boolean putAtto(PubblicazioneATM pubblicazione) {

		boolean toReturn = false;

		Map<String, Object> atto = fillAttoFromPubblicazione(pubblicazione);

		try {

			toReturn = getPutAttoClientInstance().putAtto(atto);

		} catch (Exception e) {
			log.error(e.getMessage());
		}

		log.debug("Result of putatto: " + toReturn);
		
		return toReturn;

	}

	private Map<String, Object> fillAttoFromPubblicazione(PubblicazioneATM pubblicazione) {

		Map<String, Object> atto = new HashMap<String, Object>();

		atto.put("d_dataatto", pubblicazione.getDataatto());

        atto.put("s_numeroatto", pubblicazione.getNumeroatto());

		atto.put("n_durataatto", pubblicazione.getDurataconsultazione());

		atto.put("s_titoloatto", pubblicazione.getTitolo());

		atto.put("s_oggettoatto", pubblicazione.getDescrizione());

		atto.put("s_tipoatto", pubblicazione.getTipoatto());

		atto.put("s_altroenteatto", pubblicazione.getRichiedente());

		if (pubblicazione.getFileAtto() != null) {
			FileATM fa = pubblicazione.getFileAtto();

			atto.put(
					"s_estensioneatto",
					fa.getFileallegatoname().substring(
							fa.getFileallegatoname().indexOf('.') + 1));

			atto.put(
					"f_fileatto",
					marshalingFileAtto(fa));
		}

        List<FileATM> allegati = pubblicazione.getAllegati();
        if (allegati != null && allegati.size()>0) {
			atto.put("s_allegati", marshalingFiles(allegati));
			atto.put("n_numallegatiatto", allegati.size());
		}

		return atto;
	}

	private String marshalingFileAtto(FileATM fa) {

		try {

			return new String(Base64.encodeBase64(loadBytesFile(
					fa.getFileallegato())));

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
	private String marshalingFiles(List<FileATM> files) {
		StringBuffer marshaledFile = new StringBuffer();

		if (files.size() == 0) {
			return "{}";
		}

		marshaledFile.append("[");

		for (Iterator<FileATM> i = files.iterator(); i.hasNext();) {
			FileATM a = i.next();
			String fileExtension = a.getFileallegatoname().substring(
					a.getFileallegatoname().indexOf('.')+1);

			try {

				// Base64 with second params if less than 4 no newlines
				marshaledFile
						.append("{\"s_titoloallegato\":\"")
						.append(a.getTitoloallegato())
						.append("\",\"s_estensioneallegato\":\"")
						.append(fileExtension)
						.append("\",\"f_fileallegato\":\"")
						.append(new String(Base64.encodeBase64(loadBytesFile(
								a.getFileallegato())))).append("\"}");

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
		
		log.debug("JSON File allegati:\n|" + marshaledFile.toString() + "|");

		return "\"" + marshaledFile.toString().replaceAll("\"","\\\\\"") + "\"";

	}


    private byte[] loadBytesFile(InputStream in) throws IOException {
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        while( (read=in.read(bytes)) != -1){
            out.write(Arrays.copyOfRange(bytes, 0, read));
        }
        in.close();
        out.flush();
        out.close();
        return out.toByteArray();
    }
	
}
