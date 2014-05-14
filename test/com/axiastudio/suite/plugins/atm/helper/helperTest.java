package com.axiastudio.suite.plugins.atm.helper;

import com.axiastudio.suite.plugins.atm.FileATM;
import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.ws.ATMClient;

import org.apache.log4j.Logger;
import org.junit.Assert;
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

	private Logger log = Logger.getLogger(helperTest.class);
	
	@Test
	public void testPutAtto() throws Exception {

		Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
		Date oggi = calendar.getTime();

		PubblicazioneATM p = new PubblicazioneATM();
		p.setTitolo("Verifica inserimento titolo");
		p.setDescrizione("Test con atto numero 7 di ieri " + new Date());
		// p.setInizioconsultazione(oggi);
		p.setDurataconsultazione(10);
		p.setRichiedente("Comune di Riva del Garda");
		p.setTipoatto("Determine");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        p.setDataatto(cal.getTime());
        p.setNumeroatto(7);

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

		PubblicazioneATM pubblicazione = new PubblicazioneATM();
		pubblicazione.setTitolo("Test con allegato 2");
		pubblicazione.setDescrizione("Usa uno stram non un file");
		pubblicazione.setDataatto(oggi);
		pubblicazione.setDurataconsultazione(10);
		pubblicazione.setRichiedente("Comune di Riva del Garda");
		pubblicazione.setTipoatto("Determine");

		PutAttoHelper helper = new PutAttoHelper();

		Properties ctx = loadConfig();

		helper.setup(ctx.getProperty(ATMClient.USER_ID),
				ctx.getProperty(ATMClient.PASSWORD),
				ctx.getProperty(ATMClient.MAC_NAME),
				ctx.getProperty(ATMClient.WSAKEY),
				ctx.getProperty(ATMClient.ENDPOINT));

		FileATM fileATMAtto = new FileATM();
		fileATMAtto.setTitoloallegato(pubblicazione.getTitolo());
		File fileAtto = new File("atto.pdf");
		if (fileAtto.exists()) {
			fileATMAtto.setFileallegato(new FileInputStream(fileAtto));
			fileATMAtto.setFileallegatoname("atto.pdf");
			pubblicazione.setFileAtto(fileATMAtto);
		} else {
			Assert.assertTrue("File atto.pdf not exists", false);
		}
		
		List<FileATM> allegati = new ArrayList<FileATM>();
        FileATM fileATMAllegato = new FileATM();
        fileATMAllegato.setTitoloallegato(pubblicazione.getTitolo());
        File fileAllegato = new File("allegato.pdf");
        if (fileAllegato.exists()) {
            fileATMAllegato.setFileallegato(new FileInputStream(fileAllegato));
            fileATMAllegato.setFileallegatoname("allegato.pdf");
            allegati.add(fileATMAllegato);
        } else {
            Assert.assertTrue("File allegato.pdf not exists", false);
        }
        pubblicazione.setAllegati(allegati);

		Assert.assertTrue(helper.putAtto(pubblicazione));

	}

}
