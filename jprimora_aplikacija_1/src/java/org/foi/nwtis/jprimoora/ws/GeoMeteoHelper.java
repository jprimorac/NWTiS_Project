/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimoora.ws;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.jprimora.dodaci.BazaKontroler;
import org.foi.nwtis.jprimora.dodaci.Konstante;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.jprimora.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.jprimora.rest.klijenti.GMKlijent;
import org.foi.nwtis.jprimora.rest.klijenti.OWMKlijent;
import org.foi.nwtis.jprimora.web.podaci.Adresa;
import org.foi.nwtis.jprimora.web.podaci.Lokacija;
import org.foi.nwtis.jprimora.web.podaci.MeteoPodaci;
import org.foi.nwtis.jprimora.web.slusaci.SlusacPokretac;

/**
 *
 * @author Josip
 */
public class GeoMeteoHelper {

    private BazaKontroler baza;
    
    public GeoMeteoHelper() {
        baza = new BazaKontroler(SlusacPokretac.getBP_Konfig());
    }
    
    public boolean autenticirajKorisnika(String korisnickoIme, String lozinka){
        String tipKorisnika = baza.provjeriKorisnika(korisnickoIme, lozinka);
        if(tipKorisnika.equals("ERROR")){
            return false;
        }else{
            return true;
        }
    }
    
    public boolean provjeriSredstva(String naredba, String korisnickoIme){
        float korisnikSredstva = baza.provjeriSredstva(korisnickoIme);
        float cijena = dajCijenu(naredba);
        if(korisnikSredstva>=cijena){
            return true;
        }else{
            return false;
        }

    }
    
    
    public boolean oduzmiSredstva(String naredba, String korisnickoIme){
        float cijena = dajCijenu(naredba);
        return baza.oduzmiSredstva(korisnickoIme, cijena);    
    }
    
    private float dajCijenu(String nazivNaredbe) {
        try {
            Konfiguracija konfig = SlusacPokretac.getKonfig();
            Konfiguracija cjenik = KonfiguracijaApstraktna.preuzmiKonfiguraciju(konfig.dajPostavku("direktorij") + konfig.dajPostavku("cjenik2"));
            String cijenaString = cjenik.dajPostavku(nazivNaredbe);
            float cijena = Float.parseFloat(cijenaString);
            return cijena;
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(GeoMeteoHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }
    
    public MeteoPodaci dajTrenutneMeteoPodatke(String adresa) {
        
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String apiKey = konfig.dajPostavku("apiKey");
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(adresa);

        OWMKlijent owmk = new OWMKlijent(apiKey);

        MeteoPodaci mp = owmk.getRealTimeWeather(l.getLatitude(),
                l.getLongitude());

        mp.setAdresa(adresa);
        return mp;
    }
    
    public List<Adresa> dajDodaneAdreseKorisnika(String korisnickoIme){
        return baza.dajDodaneAdreseKorisnika(korisnickoIme);
    }
    
    public List<Adresa> dajRangListuAdresa(int broj){
        return baza.dajRangListuAdresa(broj);
    }
    
    public List<MeteoPodaci> dajPosljednjihNMeteo(String adresa, int broj){
        return baza.dajPosljednjihNMeteo(adresa,broj);
    }
    
    public List<MeteoPodaci> dajPrognozuInterval(String adresa, String odtad, String dotad){
        return baza.dajPrognozuInterval(adresa,odtad, dotad);
    }
    
    public List<Adresa> dajStaniceBlizu(String adresa, String broj){
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String apiKey = konfig.dajPostavku("apiKey");
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(adresa);

        OWMKlijent owmk = new OWMKlijent(apiKey);

        return  owmk.getStationsNear(l.getLatitude(),l.getLongitude(),broj);
    }
    
    public List<MeteoPodaci> dajPrognozuSati(String adresa, int broj){
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String apiKey = konfig.dajPostavku("apiKey");
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(adresa);

        OWMKlijent owmk = new OWMKlijent(apiKey);

        return  owmk.getHoursWeather(l.getLatitude(),l.getLongitude(),broj);
    }
    
    public List<MeteoPodaci> dajPrognozuDani(String adresa, int broj){
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String apiKey = konfig.dajPostavku("apiKey");
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(adresa);

        OWMKlijent owmk = new OWMKlijent(apiKey);

        return  owmk.getDaysWeather(l.getLatitude(),l.getLongitude(),broj);
    }
}
