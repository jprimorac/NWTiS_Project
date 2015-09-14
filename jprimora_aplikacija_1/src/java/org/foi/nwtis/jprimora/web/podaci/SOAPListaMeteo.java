/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.podaci;

import java.util.List;

/**
 *
 * @author Josip
 */
public class SOAPListaMeteo {
    
    private String odgovor;
    private List<MeteoPodaci> listaMeteo;

    public SOAPListaMeteo(String odgovor, List<MeteoPodaci> listaMeteo) {
        this.odgovor = odgovor;
        this.listaMeteo = listaMeteo;
    }

    public SOAPListaMeteo() {
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public List<MeteoPodaci> getListaMeteo() {
        return listaMeteo;
    }

    public void setListaMeteo(List<MeteoPodaci> listaMeteo) {
        this.listaMeteo = listaMeteo;
    }

}
