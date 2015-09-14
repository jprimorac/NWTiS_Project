/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.web.slusaci.SlusacPokretac;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class Registracija {

    @EJB
    private KorisniciFacade korisniciFacade;

    private String korisnickoIme;
    private String lozinka;
    private String lozinka2;
    private String korisnickoGreska;
    private String lozinkaGreska;

    /**
     * Creates a new instance of Registracija
     */
    public Registracija() {
    }

    public String registrirajKorisnika() {

        if (korisnickoIme.equals("")) {
            korisnickoGreska = "Unesite korisnicko ime.";
            return "REGISTER";
        } else if (lozinka.equals("") || lozinka2.equals("")) {
            lozinkaGreska = "Unesite lozinku.";
            return "REGISTER";
        }else if(!lozinka.equals(lozinka2)){
            lozinkaGreska = "Lozinke se ne poklapaju.";
            return "REGISTER";
        } 
        else {
            if (korisniciFacade.dajKorisnikId(korisnickoIme) == -1) {
                Korisnici noviKorisnik = new Korisnici();
                noviKorisnik.setKorisnickoime(korisnickoIme);
                noviKorisnik.setLozinka(lozinka);
                noviKorisnik.setTipkorisnika("korisnik");
                noviKorisnik.setIme("?");
                noviKorisnik.setEmail("?");
                noviKorisnik.setPrezime("?");
                korisniciFacade.create(noviKorisnik);
                

                dodajUDruguAplikaciju(korisnickoIme, lozinka);

                HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
                HttpSession sesija = request.getSession();
                sesija.setAttribute("korisnik", korisnickoIme);
                sesija.setAttribute("tipKorisnika", "korisnik");

                return "OK";
            } else {
                korisnickoGreska = "Korisnik vec postoji u bazi.";
                return "REGISTER";
            }
        }
    }

    private void dodajUDruguAplikaciju(String korisnickoIme, String lozinka) {
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String adresa = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(adresa, port);

            //outputStream = new PrintWriter(socket.getOutputStream(), false);
            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "ADD " + korisnickoIme + "; PASSWD " + lozinka + ";";
            outputStream.println(komanda);
            outputStream.flush();
            socket.shutdownOutput();

            String odgovor = inputStream.readLine();

            System.out.println(odgovor);

        } catch (IOException ex) {
            Logger.getLogger(Registracija.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(Registracija.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Registracija.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    public String getLozinka2() {
        return lozinka2;
    }

    public void setLozinka2(String lozinka2) {
        this.lozinka2 = lozinka2;
    }

    public String getKorisnickoGreska() {
        return korisnickoGreska;
    }

    public void setKorisnickoGreska(String korisnickoGreska) {
        this.korisnickoGreska = korisnickoGreska;
    }

    public String getLozinkaGreska() {
        return lozinkaGreska;
    }

    public void setLozinkaGreska(String lozinkaGreska) {
        this.lozinkaGreska = lozinkaGreska;
    }

}
