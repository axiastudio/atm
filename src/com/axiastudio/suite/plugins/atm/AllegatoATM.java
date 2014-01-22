package com.axiastudio.suite.plugins.atm;

import java.io.InputStream;

/**
 * User: tiziano
 * Date: 20/01/14
 * Time: 19:58
 */
public class AllegatoATM {


    // private File fileallegato;
	private InputStream fileallegato;
    private String titoloallegato;
    private String fileallegatoname;
    
    public InputStream getFileallegato() {
        return fileallegato;
    }

    public void setFileallegato(InputStream fileallegato) {
        this.fileallegato = fileallegato;
    }


    public String getTitoloallegato() {
        return titoloallegato;
    }

    public void setTitoloallegato(String titoloallegato) {
        this.titoloallegato = titoloallegato;
    }

	public String getFileallegatoname() {
		return fileallegatoname;
	}

	public void setFileallegatoname(String fileallegatoname) {
		this.fileallegatoname = fileallegatoname;
	}
}
