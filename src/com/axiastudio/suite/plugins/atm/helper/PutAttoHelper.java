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

	private static PutAttoClient pac = null;

	public PutAttoClient getPutAttoClientInstance() {
		if (pac == null) {
			Map<String, String> context = new HashMap<String, String>();
			context.put("wsakey", "*");
			context.put("userID", "*");
			context.put("password", "*");
			context.put("MAC", "*");
			context.put("endpoint",
					"http://194.105.52.153/_wsa-t/putatto_v1.php");
			pac = new PutAttoClient(context);
		}
		return pac;
	}

	public void putAtto(PubblicazioneATM pubblicazione, List<AllegatoATM> files) {
		
		Map<String, Object> atto = fillAttoFromPubblicazione(pubblicazione, files);
		
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

		/**
		 * TODO manca valore (deve essere la data di protocollazione dell'atto)
		 */
		atto.put("d_dataatto", pubblicazione.getInizioconsultazione());

		/**
		 * TODO manca valore
		 */
		atto.put("n_durataatto", pubblicazione.getDurataconsultazione());

		// atto.put("s_titoloatto", pubblicazione.getTitolo());

		/**
		 * TODO verificare se corretto
		 */
		atto.put("s_oggettoatto", pubblicazione.getDescrizione());

		/**
		 * TODO manca valore
		 */
		atto.put("s_tipoatto", pubblicazione.getTipoatto());

		// atto.put("s_statoatto", "pubblicazione");


        if (files != null) {
			atto.put("f_fileatto", marshalingFiles(files));
		}
		
		return atto;
	}

	/**
	 * The WS actualli accept only one file, but we are readi to handle
	 * a list for furter versions.
	 * 
	 * @param files
	 * @return
	 */
	private String marshalingFiles(List<AllegatoATM> files) {
		StringBuffer marshaledFile = new StringBuffer();
		
		if (files.size() == 0) {
			return "";
		}

		for(Iterator<AllegatoATM> i = files.iterator(); i.hasNext();) {
            AllegatoATM a = i.next();
			File f = a.getFileallegato();
			String fileExtension = f.getName().substring(f.getName().indexOf('.'));
			
			marshaledFile.append("{\"s_titoloallegato\":\"").append(a.getTitoloallegato())
				.append("\",\"s_estensioneallegato\":\"").append(fileExtension)
				.append("\",\"f_fileallegato\":\"");
			
			try {

				byte[] buffer = new byte[(int) f.length()];
				DataInputStream dis = new DataInputStream(new FileInputStream(f));
				dis.read(buffer);
				dis.close();
				
				marshaledFile.append(Base64.encode(buffer)).append("\"}");
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			break;
		}
		
		return marshaledFile.toString();
	}
	
}
