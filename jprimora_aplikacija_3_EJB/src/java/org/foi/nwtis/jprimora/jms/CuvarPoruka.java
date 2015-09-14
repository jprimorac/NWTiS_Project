/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.jms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;


/**
 *
 * @author Josip
 */
@Startup
@Singleton
@LocalBean
public class CuvarPoruka {

    public CuvarPoruka() {
        poruke1 = new ArrayList<>();
        poruke2 = new ArrayList<>();
    }
    
    
    
    private List<JMSPoruka> poruke1;
    private List<JMSPoruka2> poruke2;
    private Konfiguracija konfig;
    
    public void dodajUPrve(JMSPoruka poruka){
        poruke1.add(poruka);
        System.out.println("Dodano u Prve");
    }
    
    public void dodajUDruge(JMSPoruka2 poruka){
        poruke2.add(poruka);
        obradiPoruku(poruka);
    }

    public List<JMSPoruka> getPoruke1() {
        return poruke1;
    }

    public void setPoruke1(List<JMSPoruka> poruke1) {
        this.poruke1 = poruke1;
    }

    public List<JMSPoruka2> getPoruke2() {
        return poruke2;
    }

    public void setPoruke2(List<JMSPoruka2> poruke2) {
        this.poruke2 = poruke2;
    }

    public Konfiguracija getKonfig() {
        return konfig;
    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
    }

    private void obradiPoruku(JMSPoruka2 poruka) {
        dodajAdresu(poruka.getKorisnickoIme(), poruka.getLozinka(), poruka.getAdresa());
    }
    
    public boolean provjeriAdresu(String korisnickoIme, String lozinka, String adresa) {
        String server = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "USER " + korisnickoIme + "; PASSWD " + lozinka + "; TEST \"" + adresa + "\";";
            outputStream.println(komanda);
            outputStream.flush();
            socket.shutdownOutput();

            String odgovor = inputStream.readLine();

            System.out.println(odgovor);
            if (odgovor.startsWith("OK")) {
                return true;
            }else{
                return false;
            }

        } catch (IOException ex) {
            Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }

    private void dodajAdresu(String korisnickoIme, String lozinka, String adresa) {

        if (provjeriAdresu(korisnickoIme, lozinka, adresa)) {
            return;
        }

        String server = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "USER " + korisnickoIme + "; PASSWD " + lozinka + "; ADD \"" + adresa + "\";";
            outputStream.println(komanda);
            outputStream.flush();
            socket.shutdownOutput();

            String odgovor = inputStream.readLine();

            System.out.println(odgovor);

        } catch (IOException ex) {
            Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(CuvarPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    

}
