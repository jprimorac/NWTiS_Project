/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import static java.lang.Thread.sleep;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.jprimora.dodaci.BazaKontroler;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.jprimora.rest.klijenti.OWMKlijent;
import org.foi.nwtis.jprimora.web.podaci.Adresa;
import org.foi.nwtis.jprimora.web.podaci.MeteoPodaci;

/**
 * Dretva koja radi u pozadini i u intervalima dohvaća poruke i obrađuje
 * naredbe.
 *
 * @author Josip
 */
public class PozadinskaDretva extends Thread {

    private String BPDatoteka, server, korisnik, lozinka, direktorij;
    private Konfiguracija konfig;
    private BP_Konfiguracija bp;
    private BazaKontroler baza;
    private boolean pauza;

    /**
     * Konstruktor koji prima konfiguracije
     *
     * @param konfig
     * @param bp
     */
    public PozadinskaDretva(Konfiguracija konfig, BP_Konfiguracija bp) {
        this.konfig = konfig;
        this.bp = bp;
    }

    public PozadinskaDretva() {
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda se vrti cijelo vrijeme dok je dretva aktivna. U svakom intervalu
     * pozove preuzimanje adresa te potom metodu spremiMeteoPodatke kojoj
     * proslijedi preuzete adrese.
     */
    @Override
    public void run() {
        while (true) {

            try {
                if (!pauza) {
                    baza = new BazaKontroler(bp);
                    List<Adresa> adrese = baza.preuzmiAdrese();
                    spremiMeteoPodatke(adrese);
                }

                long intervalDretve = Long.parseLong(konfig.dajPostavku("intervalDretve")) * 1000;
                sleep(intervalDretve);
            } catch (InterruptedException ex) {
                Logger.getLogger(PozadinskaDretva.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public Konfiguracija getKonfig() {
        return konfig;
    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
    }

    /**
     * Metoda primi listu adresa, te za svaku od njih preuzima meteoPodatke
     * koristeći OWMKlijent objekt kao klijent.
     *
     * @param adrese
     */
    private void spremiMeteoPodatke(List<Adresa> adrese) {
        OWMKlijent owmk = new OWMKlijent(konfig.dajPostavku("apiKey"));  //60cb7df3c4e929f89bd93012caf54c3a     7263726362

        for (Adresa a : adrese) {
            MeteoPodaci mp = owmk.getRealTimeWeather(a.getGeoloc().getLatitude(),
                    a.getGeoloc().getLongitude());

            String insertUpit = "Insert into jprimora_meteo(temperatura,vlaznost,vjetar,tlak,idadresa,oblaci) values(";
            insertUpit += mp.getTemperatureValue() + ",";
            insertUpit += mp.getHumidityValue() + ",";
            insertUpit += mp.getWindSpeedValue() + ",";
            insertUpit += mp.getPressureValue() + ",";
            insertUpit += a.getIdadresa() + ",";
            insertUpit += mp.getCloudsValue() + ")";

            baza.insertUpit(insertUpit);
            System.out.println(a.getAdresa() + " temp: " + mp.getTemperatureValue());
        }
    }

    /**
     * Metoda koja iz konfiguracijske datoteke preuzima apiKey za OpenWeatherMap
     * servis.
     *
     * @return
     */
    public String dajApiKey() {
        String apiKey = konfig.dajPostavku("apiKey");
        return apiKey;
    }

    public String getBPDatoteka() {
        return BPDatoteka;
    }

    public void setBPDatoteka(String BPDatoteka) {
        this.BPDatoteka = BPDatoteka;
    }

    public boolean isPauza() {
        return pauza;
    }

    public void setPauza(boolean pauza) {
        this.pauza = pauza;
    }

}
