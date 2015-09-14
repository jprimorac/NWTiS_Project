/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.jms;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Josip
 */
public class JMSPoruka2 implements Serializable{
    private String korisnickoIme;
    private String lozinka;
    private String adresa;
    private Date vrijeme;
    private String milis;

    public JMSPoruka2() {
    }

    public JMSPoruka2(String korisnickoIme, String lozinka, String adresa) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.adresa = adresa;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getMilis() {
        return milis;
    }

    public void setMilis(String milis) {
        this.milis = milis;
    }
    
    

}
