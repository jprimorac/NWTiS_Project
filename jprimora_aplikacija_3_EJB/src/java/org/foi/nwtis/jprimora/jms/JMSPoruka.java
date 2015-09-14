/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.jms;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;

/**
 *
 * @author Josip
 */
public class JMSPoruka implements Serializable{

    private Date vrijemePocetka;
    private Date vrijemeZavrsetka;
    private int brojPoruka;
    private int brojNWTiSPoruka;
    private List<Korisnici> dodaniKorisnici;
    private List<Korisnici> neuspjesnoKorisnici;
    private String milis;

    public JMSPoruka() {
    }

    public JMSPoruka(Date vrijemePocetka, Date vrijemeZavrsetka, int brojPoruka, int brojNWTiSPoruka, List<Korisnici> dodaniKorisnici, List<Korisnici> neuspjesnoKorisnici) {
        this.vrijemePocetka = vrijemePocetka;
        this.vrijemeZavrsetka = vrijemeZavrsetka;
        this.brojPoruka = brojPoruka;
        this.brojNWTiSPoruka = brojNWTiSPoruka;
        this.dodaniKorisnici = dodaniKorisnici;
        this.neuspjesnoKorisnici = neuspjesnoKorisnici;
    }

    public Date getVrijemePocetka() {
        return vrijemePocetka;
    }

    public void setVrijemePocetka(Date vrijemePocetka) {
        this.vrijemePocetka = vrijemePocetka;
    }

    public Date getVrijemeZavrsetka() {
        return vrijemeZavrsetka;
    }

    public void setVrijemeZavrsetka(Date vrijemeZavrsetka) {
        this.vrijemeZavrsetka = vrijemeZavrsetka;
    }

    public int getBrojPoruka() {
        return brojPoruka;
    }

    public void setBrojPoruka(int brojPoruka) {
        this.brojPoruka = brojPoruka;
    }

    public int getBrojNWTiSPoruka() {
        return brojNWTiSPoruka;
    }

    public void setBrojNWTiSPoruka(int brojNWTiSPoruka) {
        this.brojNWTiSPoruka = brojNWTiSPoruka;
    }

    public List<Korisnici> getDodaniKorisnici() {
        return dodaniKorisnici;
    }

    public void setDodaniKorisnici(List<Korisnici> dodaniKorisnici) {
        this.dodaniKorisnici = dodaniKorisnici;
    }

    public List<Korisnici> getNeuspjesnoKorisnici() {
        return neuspjesnoKorisnici;
    }

    public void setNeuspjesnoKorisnici(List<Korisnici> neuspjesnoKorisnici) {
        this.neuspjesnoKorisnici = neuspjesnoKorisnici;
    }

    public String getMilis() {
        return milis;
    }

    public void setMilis(String milis) {
        this.milis = milis;
    }
    

}
