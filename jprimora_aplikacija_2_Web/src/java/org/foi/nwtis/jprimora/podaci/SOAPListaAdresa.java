/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.podaci;

import java.util.List;

/**
 *
 * @author Josip
 */
public class SOAPListaAdresa {
    
    private String odgovor;
    private List<Adresa> adrese;

    public SOAPListaAdresa(String odgovor, List<Adresa> adrese) {
        this.odgovor = odgovor;
        this.adrese = adrese;
    }

    public SOAPListaAdresa() {
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public List<Adresa> getAdrese() {
        return adrese;
    }

    public void setAdrese(List<Adresa> adrese) {
        this.adrese = adrese;
    }
    
}
