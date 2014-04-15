package com.axiastudio.suite.plugins.atm.helper;

import com.axiastudio.suite.plugins.atm.AllegatoATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

	@Test
	@Ignore
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
		p.setTitolo("Test con allegato");
		p.setDescrizione("Controllare il file allegato");
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

		List<AllegatoATM> allegati = new ArrayList<AllegatoATM>();

		AllegatoATM allegato = new AllegatoATM();
		allegato.setTitoloallegato("Allegato di prova");

		File fileAllegato = new File("allegato.txt");
		if (fileAllegato.exists()) {

			allegato.setFileallegato(new FileInputStream(fileAllegato));
			allegato.setFileallegatoname("allegato.txt");

		}

		allegati.add(allegato);

		helper.putAtto(p, allegati);

		Assert.assertTrue(true);

	}

}
