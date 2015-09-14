/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.dodaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.jprimora.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Josip
 */
public class ObradaKorisnika {

    private Konfiguracija konfig;
    private BP_Konfiguracija bp;
    private BazaKontroler baza;
    private Matcher regex;
    private String odgovor = "";

    public ObradaKorisnika(Konfiguracija konfig, BP_Konfiguracija bp, Matcher regex) {
        this.konfig = konfig;
        this.bp = bp;
        this.regex = regex;
    }

    /**
     * Metoda obradjuje naredbu korisnika. Ovisno o grupi 3 sadrzaja naredbe
     * pozivaju se razlicite metode.
     *
     * @return
     */
    public String obradiKorisnika() {

        baza = new BazaKontroler(bp);

        String naredba = regex.group(3);
        if (naredba.equals(" TYPE;")) {
            odgovor = vratiTipKorisnika(regex.group(1), regex.group(2));
        } else if (naredba.startsWith(" ADD")) {
            odgovor = dodajAdresu(regex.group(4));
        } else if (naredba.startsWith(" TEST")) {
            odgovor = testirajAdresu(regex.group(5));
        } else if (naredba.startsWith(" GET")) {
            odgovor = dajMeteoPodatke(regex.group(6));
        }
        return odgovor;
    }

    private String vratiTipKorisnika(String korisnickoIme, String lozinka) {
        String tekstProvjere = baza.provjeriKorisnika(korisnickoIme, lozinka);
        switch (tekstProvjere) {
            case "USER":
                return "OK 10;";
            case "ADMIN":
                return "OK 11;";
        }
        return null;
    }

    private String dodajAdresu(String adresa) {
        float sredstva = baza.provjeriSredstva(regex.group(1));
        float cijena = dajCijenu("ADD");
        if (cijena > sredstva) {
            return Konstante.error40;
        } else {
            int idAdrese = baza.preuziIdAdrese(adresa);
            if (idAdrese != -1) {
                return Konstante.error41;
            } else {
                int idKorisnika = baza.preuzmiIdKorisnika(regex.group(1));
                baza.spremiAdresu(adresa, idKorisnika);
                baza.oduzmiSredstva(regex.group(1), cijena);
                return "OK 10;";
            }
        }

    }

    private String testirajAdresu(String adresa) {
        float sredstva = baza.provjeriSredstva(regex.group(1));
        float cijena = dajCijenu("TEST");
        if (cijena > sredstva) {
            return Konstante.error40;
        } else {
            baza.oduzmiSredstva(regex.group(1), cijena);
            int idAdrese = baza.preuziIdAdrese(adresa);
            if (idAdrese == -1) {
                return Konstante.error42;
            } else {
                return "OK 10;";
            }
        }
    }

    private String dajMeteoPodatke(String adresa) {
        float sredstva = baza.provjeriSredstva(regex.group(1));
        float cijena = dajCijenu("GET");
        if (cijena > sredstva) {
            return Konstante.error40;
        } else {
            int idAdrese = baza.preuziIdAdrese(adresa);
            if (idAdrese == -1) {
                return Konstante.error43;
            } else {
                String apiKey = konfig.dajPostavku("apiKey");
                String drugiDio = baza.preuzmiTrenutneMeteoPodatkeString(adresa, apiKey);
                //TODO provjeriti hoću space ili enter koristit.
                return "OK 10; " + drugiDio;
            }
        }
    }

    private float dajCijenu(String nazivNaredbe) {
        try {
            Konfiguracija cjenik = KonfiguracijaApstraktna.preuzmiKonfiguraciju(konfig.dajPostavku("direktorij") + konfig.dajPostavku("cjenik1"));
            String cijenaString = cjenik.dajPostavku(nazivNaredbe);
            float cijena = Float.parseFloat(cijenaString);
            return cijena;
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(ObradaKorisnika.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;

    }
    
    

}
