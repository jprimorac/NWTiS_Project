/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.zrna;

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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foi.nwtis.jprimora.ejb.eb.Adresa;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.eb.Lokacija;
import org.foi.nwtis.jprimora.jms.CuvarPoruka;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.slusaci.SlusacAplikacje;

/**
 *
 * @author Josip
 */
public class KonzolaHelper {

    private Konfiguracija konfig;
    private static final String BASE_URI = "http://localhost:8080/jprimora_aplikacija_2_Web/webresources/";

    public KonzolaHelper() {
        this.konfig = SlusacAplikacje.dajKonfig();
    }

    public void dodajKorisnika(String korisnickoIme, String lozinka) {
        String server = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;

        try {
            socket = new Socket(server, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "ADD " + korisnickoIme + "; PASSWD " + lozinka + ";";
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

    public List<Korisnici> vratiAktivneKorisnike() {

        Client client = ClientBuilder.newClient();

        WebTarget webResource = client.target(BASE_URI).path("ms");

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        List<Korisnici> korisniciLista = new ArrayList<>();
        try {
            JSONObject JOdgovor;

            JOdgovor = new JSONObject(odgovor);
            JSONArray korisnici = JOdgovor.getJSONArray("korisnici");
            for (int i = 0; i < korisnici.length(); i++) {
                Korisnici korisnik = new Korisnici();
                JSONObject JKorisnik = korisnici.getJSONObject(i);
                korisnik.setIdkorisnik(JKorisnik.getInt("idkorisnik"));
                korisnik.setKorisnickoime(JKorisnik.getString("korisnickoIme"));
                korisnik.setLozinka(JKorisnik.getString("lozinka"));
                korisnik.setIme(JKorisnik.getString("ime"));
                korisnik.setPrezime(JKorisnik.getString("prezime"));
                korisnik.setEmail(JKorisnik.getString("email"));
                korisnik.setTipkorisnika(JKorisnik.getString("tipKorisnika"));
                korisniciLista.add(korisnik);
            }
        } catch (JSONException ex) {
            Logger.getLogger(KonzolaHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return korisniciLista;
    }
    
    public List<Adresa> dohvatiAdreseKorisnika(int id){
        
        Client client = ClientBuilder.newClient();

        WebTarget webResource = client.target(BASE_URI).path("ms/"+id);

        String odgovor = webResource.request(MediaType.APPLICATION_JSON).get(String.class);
        List<Adresa> adrese = new ArrayList<>();
        try {
            JSONObject JOdgovor;

            JOdgovor = new JSONObject(odgovor);
            JSONArray korisnici = JOdgovor.getJSONArray("adrese");
            for (int i = 0; i < korisnici.length(); i++) {
                Adresa adresa = new Adresa();
                Lokacija loc = new Lokacija();
                JSONObject JAdresa = korisnici.getJSONObject(i);
                adresa.setAdresa(JAdresa.getString("adresa"));
                loc.setLatitude(JAdresa.getString("lat"));
                loc.setLongitude(JAdresa.getString("long"));
                adresa.setGeoloc(loc);
                adresa.setIdadresa(JAdresa.getInt("idadresa"));
                adrese.add(adresa);
            }
        } catch (JSONException ex) {
            Logger.getLogger(KonzolaHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return adrese;
    }
    
    public String dohvatiPrognozu(String korisnickoIme, String lozinka, String adresa){
        String server = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;
        String odgovor = "";

        try {
            socket = new Socket(server, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            String komanda = "USER " + korisnickoIme + "; PASSWD " + lozinka + "; GET \"" + adresa +"\";";
            outputStream.println(komanda);
            outputStream.flush();
            socket.shutdownOutput();

            odgovor = inputStream.readLine();
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
        return odgovor;
    }
   
    public String obradiNaredbu(String naredba){
        String server = konfig.dajPostavku("server");
        int port = Integer.parseInt(konfig.dajPostavku("port"));
        BufferedReader inputStream = null;
        PrintWriter outputStream = null;
        Socket socket = null;
        String odgovor = "";
        try {
            socket = new Socket(server, port);

            outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            outputStream.println(naredba);
            outputStream.flush();
            socket.shutdownOutput();

            odgovor = inputStream.readLine();
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
        return odgovor;
    }
}
