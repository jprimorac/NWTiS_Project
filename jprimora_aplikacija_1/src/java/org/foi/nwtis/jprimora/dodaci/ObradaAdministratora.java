/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.dodaci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.jprimora.web.slusaci.DretvaObrada;
import org.foi.nwtis.jprimora.web.slusaci.ServerSucelje;

/**
 *
 * @author Josip
 */
public class ObradaAdministratora {

    private Konfiguracija konfig;
    private BP_Konfiguracija bp;
    private Matcher regex;
    private String primljenaDatoteka = "";
    private String odgovor = "";
    private ServerSucelje server;
    private DretvaObrada dretvaObrada;
    private BufferedReader inputStream;
    private BazaKontroler baza;

    public ObradaAdministratora(Konfiguracija konfig, BP_Konfiguracija bp, Matcher regex, ServerSucelje server, DretvaObrada dretvaObrada, BufferedReader inputStream) {
        this.konfig = konfig;
        this.bp = bp;
        this.regex = regex;
        this.server = server;
        this.dretvaObrada = dretvaObrada;
        this.inputStream = inputStream;
    }

    /**
     * Metoda obradjuje naredbu administratora. Ovisno o grupi 3 sadrzaja naredbe
     * pozivaju se razlicite metode.
     *
     * @return
     */
    public String obradiAdministratora() {

        baza = new BazaKontroler(bp);
        
        String naredba = regex.group(3);
        switch (naredba) {
            case " PAUSE":
                odgovor = pauzirajServera();
                break;
            case " START":
                odgovor = startajServera();
                break;
            case " STOP":
                odgovor = stopirajServera();
                break;
            case " DOWNLOAD":
                odgovor = downloadDatoteke();
                break;
        }
        if (naredba.contains(" ADMIN")) {
            odgovor = dodajAdmina(regex.group(4));
        } else if (naredba.contains(" NOADMIN")) {
            odgovor = oduzmiAdmina(regex.group(5));
        } else if (naredba.contains(" UPLOAD")) {
            long velicinaDatoteke = Long.parseLong(regex.group(6));
            odgovor = uploadDatoteke(velicinaDatoteke);
        }
        return odgovor;
    }

    /**
     * Metoda preuzima sadrzaj datoteke preko ulaznog toka sa soketa.
     * Usporedjuje duzinu primljenog Stringa s primljenim parametrom
     * velicinaDatoteke. Ukoliko je jednaka sprema datoteku.
     *
     * @param velicinaDatoteke
     * @return
     */
    private String uploadDatoteke(long velicinaDatoteke) {
        try {
            String sadržajDatoteke = "";
            String pomocna;
            while ((pomocna = inputStream.readLine()) != null) {
                sadržajDatoteke += pomocna;
            }
            if (sadržajDatoteke.length() != velicinaDatoteke) {
                return Konstante.error37;
            }
            String imeDatoteke = konfig.dajPostavku("direktorij") + konfig.dajPostavku("cjenik1");
            File datoteka = new File(imeDatoteke);

            Base64.Decoder decoder = Base64.getDecoder();
            byte[] datotekaByte = decoder.decode(sadržajDatoteke);
            FileOutputStream fileOutputStreamWriter = new FileOutputStream(imeDatoteke);
            fileOutputStreamWriter.write(datotekaByte);
        } catch (IOException ex) {
            Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }

    /**
     * Metoda provjerava da li postoji datoteta koja sadrzi konfiguraciju
     * servera. Ukoliko postoji, salje njen sadrzaj preko izlaznog toka
     * prethodnim kodiranjem u Base64 format.
     *
     * @return
     */
    private String downloadDatoteke() {
        String imeDatoteke = konfig.dajPostavku("direktorij") + konfig.dajPostavku("cjenik1");
        String konfiguracijaUBase64 = null;
        try {
            File datoteka = new File(imeDatoteke);
            FileInputStream fileInputStreamReader = new FileInputStream(datoteka);
            byte[] bytes = new byte[(int) datoteka.length()];
            fileInputStreamReader.read(bytes);
            Base64.Encoder encoder = Base64.getEncoder();
            String sadrzajUBase64 = encoder.encodeToString(bytes);
            return "DATA " + sadrzajUBase64.length() + ";\r\n" + sadrzajUBase64;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "ERROR: Nije moguce dohvatiti datoteku.";
        }

    }

    private String dodajAdmina(String korisnickoIme) {
        baza = new BazaKontroler(bp);
        return baza.dodajAdmina(korisnickoIme);
    }

    private String oduzmiAdmina(String korisnickoIme) {
        baza = new BazaKontroler(bp);
        return baza.oduzmiAdmina(korisnickoIme);
    }

    /**
     * Metoda pauzira servera ukoliko se trenutno nalazi u normalnom stanju.
     * Ukoliko nije, vraća gresku.
     *
     * @return
     */
    private String pauzirajServera() {
        if (server.isPauza()) {
            return Konstante.error30;
        } else {
            server.setPauza(true);
            return "OK 10;";
        }
    }

    /**
     * Metoda starta servera ukoliko se nalazi u stanju pauze. Ukoliko nije,
     * vraca gresku.
     *
     * @return
     */
    private String startajServera() {
        if (server.isPauza()) {
            server.startaj();
            return "OK 10;";
        } else {
            return Konstante.error31;
        }
    }

    /**
     * Metoda gasi servera tako da ga prvo postavlja u stanje pauze. Potom
     * poziva serijalizaciju pa gasenje servera. Nazalost, ne evidentira tj.
     * serijalizira se zahtjev za gasenje servera.
     *
     * @return
     */
    private String stopirajServera() {
        if (!server.isStopiran()) {
            server.stopiraj();
            return "OK 10;";
        }
        return Konstante.error32;
    }
    
    

    /**
     * Metoda mjenja ime dadoteke (String) tako da mu prije ekstenzije dodaje
     * trenutno virjeme u formatu YYYYMMdd_hhmmss.
     *
     * @param imeDatoteke
     * @return
     */
    private String promjeniIme(String imeDatoteke) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd_hhmmss");
        String vrijemeString = sdf.format(System.currentTimeMillis());

        String pocetakImena = imeDatoteke.substring(0, imeDatoteke.length() - 4);
        pocetakImena += vrijemeString;
        String ekstenzija = imeDatoteke.substring(imeDatoteke.length() - 4, imeDatoteke.length());

        imeDatoteke = pocetakImena + ekstenzija;

        return imeDatoteke;
    }
    
    

}
