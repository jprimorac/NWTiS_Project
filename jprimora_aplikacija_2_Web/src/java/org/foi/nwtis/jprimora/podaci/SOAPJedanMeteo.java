/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.podaci;

/**
 *
 * @author Josip
 */
public class SOAPJedanMeteo {
    
    private String odgovor;
    private MeteoPodaci meteo;

    public SOAPJedanMeteo(String odgovor, MeteoPodaci meteo) {
        this.odgovor = odgovor;
        this.meteo = meteo;
    }

    public SOAPJedanMeteo() {
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public MeteoPodaci getMeteo() {
        return meteo;
    }

    public void setMeteo(MeteoPodaci meteo) {
        this.meteo = meteo;
    }
    
}
