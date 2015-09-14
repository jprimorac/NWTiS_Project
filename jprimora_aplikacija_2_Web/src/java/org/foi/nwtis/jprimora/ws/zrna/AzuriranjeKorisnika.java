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
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
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
@RequestScoped
public class AzuriranjeKorisnika {
    @EJB
    private KorisniciFacade korisniciFacade;

    private PregledKorisnika pregledKorisnika;
    private Korisnici korisnik;
    private String tipKorisnika;
    /**
     * Creates a new instance of AziriranjeKorisnika
     */
    public AzuriranjeKorisnika() {
    }
    
    @PostConstruct
    private void dohvatiKorisnika(){
        pregledKorisnika = (PregledKorisnika) 
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .get("pregledKorisnika");
        
        korisnik = pregledKorisnika.getOdabraniKorisnik();
        tipKorisnika = korisnik.getTipkorisnika();
        
    }
    
    public String azurirajKorisnika(){
        korisniciFacade.updateKorisnik(korisnik);
        if(!tipKorisnika.equals(korisnik.getTipkorisnika())){
            String naredba ="";
            if(tipKorisnika.equals("admin")){
                naredba = "NOADMIN";
            }else if(tipKorisnika.equals("korisnik")){
                naredba = "ADMIN";
            }
            promjeniTipUDrugoj(naredba);
        }
        pregledKorisnika.dohvatiKorisnike();
        return "OK";
    }

    public Korisnici getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnici korisnik) {
        this.korisnik = korisnik;
    }
    
    private void promjeniTipUDrugoj(String naredba){
        
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        String korisnickoIme = (String) sesija.getAttribute("korisnik");
        
        Korisnici prijavljeniAdmin = korisniciFacade.dajKorisnik(korisnickoIme);
        
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String adresa = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;
        
        

        try {
            socket = new Socket(adresa, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "USER " + prijavljeniAdmin.getKorisnickoime() + "; PASSWD " + prijavljeniAdmin.getLozinka() + "; " + naredba+ " " + korisnik.getKorisnickoime() +  ";" ;
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
    
}
