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
public class Dnevnik {
    private int id;
    private String ipAdresa;
    private String korisnik;
    private int korisnikId;
    private String Zahtjev;
    private String Odgovor;
    private int status;
    private int trajanje;
    private String URL;
    private String vrijeme;
    private float sredstva;

    public Dnevnik() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public int getKorisnikId() {
        return korisnikId;
    }

    public void setKorisnikId(int korisnikId) {
        this.korisnikId = korisnikId;
    }

    public String getZahtjev() {
        return Zahtjev;
    }

    public void setZahtjev(String Zahtjev) {
        this.Zahtjev = Zahtjev;
    }

    public String getOdgovor() {
        return Odgovor;
    }

    public void setOdgovor(String Odgovor) {
        this.Odgovor = Odgovor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(int trajanje) {
        this.trajanje = trajanje;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(String vrijeme) {
        this.vrijeme = vrijeme;
    }

    public float getSredstva() {
        return sredstva;
    }

    public void setSredstva(float sredstva) {
        this.sredstva = sredstva;
    }
    
     
}
