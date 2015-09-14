/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.podaci;

import java.util.Date;
import javax.mail.Flags;

/**
 *
 * @author jprimora
 */
public class Poruka {
    private String id;
    private Date vrijeme;
    private String salje;
    private String predmet;
    private String vrsta;
    private int velicina;    
    private int brojPrivitaka;
    private Flags zastavice;
    private boolean brisati;
    private boolean procitano;
    private String sadrzaj;

    public Poruka(String id, String sadrzaj, Date poslano, String salje, String predmet, String vrsta, int velicina, int brojPrivitaka, Flags zastavice, boolean brisati, boolean procitano) {
        this.id = id;
        this.vrijeme = poslano;
        this.salje = salje;
        this.predmet = predmet;
        this.vrsta = vrsta;
        this.velicina = velicina;
        this.brojPrivitaka = brojPrivitaka;
        this.zastavice = zastavice;
        this.brisati = brisati;
        this.procitano = procitano;
        this.sadrzaj=sadrzaj;
    }

    public Poruka() {
        
    }

    public String getId() {
        return id;
    }

    public boolean isBrisati() {
        return brisati;
    }

    public void setBrisati(boolean brisati) {
        this.brisati = brisati;
    }

    public int getBrojPrivitaka() {
        return brojPrivitaka;
    }

    public Flags getZastavice() {
        return zastavice;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public String getPredmet() {
        return predmet;
    }

    public boolean isProcitano() {
        return procitano;
    }

    public void setProcitano(boolean procitano) {
        this.procitano = procitano;
    }
    
    public String getSalje() {
        return salje;
    }

    public String getVrsta() {
        return vrsta;
    }

    public int getVelicina() {
        return velicina;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public void setZastavice(Flags zastavice) {
        this.zastavice = zastavice;
    } 
}
