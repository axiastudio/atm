package com.axiastudio.suite.plugins.atm.helper;

import com.axiastudio.suite.plugins.atm.PubblicazioneATM;
import com.axiastudio.suite.plugins.atm.helper.PutAttoHelper;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * User: tiziano
 * Date: 20/01/14
 * Time: 19:38
 */
public class helperTest {

    @Test
    public void testPutAtto() throws Exception {

        Calendar calendar = Calendar.getInstance(Locale.ITALIAN);
        Date oggi = calendar.getTime();

        PubblicazioneATM p = new PubblicazioneATM();
        p.setTitolo("Primo tentativo");
        p.setDescrizione("Primo tentativo di inserimento su Albo");
        p.setInizioconsultazione(oggi);
        p.setDurataconsultazione(10);
        p.setRichiedente("Comune di Riva del Garda");
        p.setTipoatto("determina");

        PutAttoHelper helper = new PutAttoHelper();
        helper.putAtto(p);


    }

}
