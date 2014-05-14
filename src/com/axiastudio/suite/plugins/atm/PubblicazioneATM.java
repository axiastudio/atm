package com.axiastudio.suite.plugins.atm;

import java.util.Date;
import java.util.List;

/**
 * User: tiziano
 * Date: 20/01/14
 * Time: 19:53
 */
public class PubblicazioneATM {

    private String descrizione;
    private Date dataatto;
    private Integer numeroatto;
    private Integer durataconsultazione;
    private String richiedente;
    private String tipoatto;
	private FileATM fileAtto;
    private List<FileATM> allegati;

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDataatto(Date dataatto) {
        this.dataatto = dataatto;
    }

    public Date getDataatto() {
        return dataatto;
    }

    public Integer getNumeroatto() {
        return numeroatto;
    }

    public void setNumeroatto(Integer numeroatto) {
        this.numeroatto = numeroatto;
    }

    public void setDurataconsultazione(Integer durataconsultazione) {
        this.durataconsultazione = durataconsultazione;
    }

    public Integer getDurataconsultazione() {
        return durataconsultazione;
    }

    public void setRichiedente(String richiedente) {
        this.richiedente = richiedente;
    }

    public String getRichiedente() {
        return richiedente;
    }

    public void setTipoatto(String tipoatto) {
        this.tipoatto = tipoatto;
    }

    public String getTipoatto() {
        return tipoatto;
    }

    public void setFileAtto(FileATM fileAtto) {
    	this.fileAtto = fileAtto;
    }
    
	public FileATM getFileAtto() {
		return this.fileAtto;
	}

    public List<FileATM> getAllegati() {
        return allegati;
    }

    public void setAllegati(List<FileATM> allegati) {
        this.allegati = allegati;
    }
}
