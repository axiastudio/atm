package com.axiastudio.suite.plugins.atm.helper;

import com.axiastudio.suite.plugins.atm.AllegatoATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

/**
 * User: tiziano Date: 20/01/14 Time: 19:38
 */
public class helperTest {

	private Logger log = Logger.getLogger(helperTest.class);
	
	@Test
	public void testPutAtto() throws Exception {

		Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
		Date oggi = calendar.getTime();

		PubblicazioneATM p = new PubblicazioneATM();
		p.setTitolo("Altro tentativo");
		p.setDescrizione("Ulteriore tentativo di inserimento su Albo");
		// p.setInizioconsultazione(oggi);
		p.setDurataconsultazione(10);
		p.setRichiedente("Comune di Riva del Garda");
		p.setTipoatto("Determine");

		PutAttoHelper helper = new PutAttoHelper();

		Properties ctx = loadConfig();

		helper.setup(ctx.getProperty(ATMClient.USER_ID),
				ctx.getProperty(ATMClient.PASSWORD),
				ctx.getProperty(ATMClient.MAC_NAME),
				ctx.getProperty(ATMClient.WSAKEY),
				ctx.getProperty(ATMClient.ENDPOINT));

		boolean res = helper.putAtto(p);
		
		if (res) {
			System.out.println("Done");
		} else {
			System.out.println("Something is gone wrong");
		}

	}

	private Properties loadConfig() {
		Properties p = new Properties();

		try {
			p.load(getClass().getResourceAsStream("putatto.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return p;

	}

	@Test
	public void testPutAttoAllegato() throws Exception {

		Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
		Date oggi = calendar.getTime();

		PubblicazioneATM p = new PubblicazioneATM();
		p.setTitolo("Test con allegato 2");
		p.setDescrizione("Usa uno stram non un file");
		p.setDataatto(oggi);
		p.setDurataconsultazione(10);
		p.setRichiedente("Comune di Riva del Garda");
		p.setTipoatto("Determine");

		PutAttoHelper helper = new PutAttoHelper();

		Properties ctx = loadConfig();

		helper.setup(ctx.getProperty(ATMClient.USER_ID),
				ctx.getProperty(ATMClient.PASSWORD),
				ctx.getProperty(ATMClient.MAC_NAME),
				ctx.getProperty(ATMClient.WSAKEY),
				ctx.getProperty(ATMClient.ENDPOINT));

		AllegatoATM fileAtto = new AllegatoATM();
		fileAtto.setTitoloallegato(p.getTitolo());

		File fileAllegato = new File("allegato.pdf");
		if (fileAllegato.exists()) {

			fileAtto.setFileallegato(new FileInputStream(fileAllegato));
			fileAtto.setFileallegatoname("allegato.pdf");

			p.setFileAtto(fileAtto);
		
		} else {
			Assert.assertTrue("File allegato.pdf not exists", false);
		}
		
		List<AllegatoATM> allegati = new ArrayList<AllegatoATM>();

		Assert.assertTrue(helper.putAtto(p, allegati));

	}

}
