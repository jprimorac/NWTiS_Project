/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
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
public class AzuriranjeCjenika {

    @EJB
    private KorisniciFacade korisniciFacade;

    private Korisnici adminKorisnik;
    private String cjenik_tekst;
    private String cjenikDatoteka;
    private Konfiguracija konfig;

    /**
     * Creates a new instance of AzuriranjeCjenika
     */
    public AzuriranjeCjenika() {
    }


    @PostConstruct
    private void dohvatiAdminaCjenik() {
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        String koriscnickoIme = (String) sesija.getAttribute("korisnik");
        adminKorisnik = korisniciFacade.dajKorisnik(koriscnickoIme);

        konfig = SlusacPokretac.getKonfig();

        dohvatiCjenik();
    }


    public String getCjenik_tekst() {
        return cjenik_tekst;
    }

    public void setCjenik_tekst(String cjenik_tekst) {
        this.cjenik_tekst = cjenik_tekst;
    }

    private void dohvatiCjenik() {
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
            String komanda = "USER " + adminKorisnik.getKorisnickoime() + "; PASSWD " + adminKorisnik.getLozinka() + "; DOWNLOAD;";
            outputStream.println(komanda);
            outputStream.flush();
            socket.shutdownOutput();

            String odgovor = inputStream.readLine();
            boolean jeDownload = odgovor.contains("DATA");
            System.out.println(odgovor);
            String privremeno = "";
            String ostatakPrimljenog = "";
            while ((privremeno = inputStream.readLine()) != null) {
                ostatakPrimljenog += privremeno;
            }
            if (jeDownload) {
                int velicinaDatoteke = Integer.parseInt(odgovor.substring(5, odgovor.length() - 1));
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] datotekaByte = decoder.decode(ostatakPrimljenog);
                cjenik_tekst = new String(datotekaByte);
            } else if (!ostatakPrimljenog.equals("")) {
                System.out.println(ostatakPrimljenog);
            }

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

    public void posaljiCjenik() {
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
            
            
            byte[] bytes = cjenik_tekst.getBytes();

            Base64.Encoder encoder = Base64.getEncoder();
            String sadrzajUBase64 = encoder.encodeToString(bytes);
            
            String komanda = "USER " + adminKorisnik.getKorisnickoime() + "; PASSWD " + adminKorisnik.getLozinka() + "; UPLOAD " + sadrzajUBase64.length() + ";\n" + sadrzajUBase64;
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
