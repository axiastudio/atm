package com.axiastudio.suite.plugins.atm;

import java.util.Date;

/**
 * User: tiziano
 * Date: 20/01/14
 * Time: 19:53
 */
public class PubblicazioneATM {


    private String titolo;
    private String descrizione;
    private Date inizioconsultazione;
    private Integer durataconsultazione;
    private String richiedente;
    private String tipoatto;

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setInizioconsultazione(Date inizioconsultazione) {
        this.inizioconsultazione = inizioconsultazione;
    }

    public Date getInizioconsultazione() {
        return inizioconsultazione;
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
}
