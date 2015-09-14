/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.jprimora.dodaci.BazaKontroler;
import org.foi.nwtis.jprimora.dodaci.Konstante;
import org.foi.nwtis.jprimora.dodaci.ObradaAdministratora;
import org.foi.nwtis.jprimora.dodaci.ObradaKorisnika;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa obradjuje zahtjeve upucene prema serveru. Nasljedjuje klasu Thread.
 * Moze imati dva stanja: Slobodna i Zauzeta.
 *
 * @author Josip
 */
public class DretvaObrada extends Thread {

    public enum StanjeDretve {

        Slobodna, Zauzeta
    };

    private Konfiguracija konfig;
    private BP_Konfiguracija bp;
    private BazaKontroler baza;
    private StanjeDretve stanje;
    private Socket socket;
    private BufferedReader inputStream = null;
    private PrintWriter outputStream = null;
    private Matcher regex;
    private String primljenaDatoteka = "";
    private ServerSucelje server;
    private boolean pozoviStopiranjeServera = false;
    private String primljeneKomande = "";
    private String odgovor = "";

    /**
     * Konstruktor koji prima parametre ThreadGroup (grupa dretvi kojoj ce ova
     * pripadati) te ime name(ime dretve).
     *
     * @param group
     * @param name
     */
    public DretvaObrada(ThreadGroup group, String name) {
        super(group, name);
    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
        this.stanje = StanjeDretve.Slobodna;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Dretva se tokom rada se nalazi u beskonačnoj petlji. Na kraju petlje
     * nalazi se wait(). Da bi dretva nastavila s radom, potrebno je pozvati
     * metodu prekiniCekanje. U svakom ciklusu obrade dretva provjerava
     * primljene komande. Ukoliko su komande ispravne, poziva obradiKorisnika
     * ili obradiAdministratora ovisno o sadrzaju naredbe. Ukoliko naredba ne
     * zadovoljava dozvoljene izraze, vraca odgovarajucu gresku onom tko joj je
     * poslao zahtjev.
     */
    @Override
    public synchronized void run() {
        while (true) {

            try {
                outputStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
                inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

                primljeneKomande = inputStream.readLine();
                long vrijemeZahtjeva = System.currentTimeMillis();

                if (!provjeriKomande(primljeneKomande)) {
                    odgovor = Konstante.error90;
                }
                outputStream.println(odgovor);
                outputStream.flush();
                String ipAdresa = socket.getRemoteSocketAddress().toString();
                socket.shutdownOutput();
                System.out.println(odgovor);
                long vrijemeObrade = System.currentTimeMillis() - vrijemeZahtjeva;
                evidentirajZahtjev(ipAdresa, vrijemeObrade, primljeneKomande);

                if (pozoviStopiranjeServera) {
                    server.stopiraj();
                }

            } catch (IOException ex) {
                Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
            }

            setStanje(StanjeDretve.Slobodna);
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metoda zatvara ulazno/izlazne tokove, te socket trenutno otvoren. Poziva
     * se na kraju svake obrade ili prije po potrebi.
     */
    private void zatvaranjeIO() {
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Metoda prekida cekanje (wait) unutar run metode.
     */
    public synchronized void prekiniCekanje() {
        notify();
    }

    /**
     * Metoda prima String komande i provjerava da li zadovoljava definirane
     * dozvoljene izraze. Ukoliko ne zadovolja, vraca null.
     *
     * @param komande
     * @return
     */
    public boolean provjeriKomande(String komande) {
        String sintaksa = "USER ([\\w|-]+); PASSWD ([\\w|-|#|!]+);( PAUSE| START| STOP| ADMIN ([\\w|-]+)| NOADMIN ([\\w|-]+)| UPLOAD (\\d+)| DOWNLOAD);";
        Pattern pattern = Pattern.compile(sintaksa);
        regex = pattern.matcher(komande);
        if (regex.matches()) {
            String admin = provjeriKorisnika(regex.group(1), regex.group(2));
            if (admin.equals("ERROR")) {
                odgovor = Konstante.error20;
                return true;
            } else if (admin.equals("USER")) {
                odgovor = Konstante.error21;
                return true;
            }
            ObradaAdministratora obradaAdmina = new ObradaAdministratora(konfig, bp, regex, server, this, inputStream);
            odgovor = obradaAdmina.obradiAdministratora();
            return true;
        }

        sintaksa = "USER ([\\w|-]+); PASSWD ([\\w|-|#|!]+);( ADD \\\"(.+)\\\";| TEST \\\"(.+)\\\";| GET \\\"(.+)\\\";| TYPE;)?";
        pattern = Pattern.compile(sintaksa);
        regex = pattern.matcher(komande);
        if (regex.matches()) {
            if(server.isStopiran()){
                odgovor = "Server stopiran.";
                return true;
            }
            if (provjeriKorisnika(regex.group(1), regex.group(2)).equals("ERROR")) {
                odgovor = Konstante.error20;
                return true;
            } else if (regex.group(3) == null) {
                odgovor = "OK 10;";
                return true;
            } else {
                ObradaKorisnika obradaKorisnika = new ObradaKorisnika(konfig, bp, regex);
                odgovor = obradaKorisnika.obradiKorisnika();
            }
            return true;
        }

        sintaksa = "ADD ([\\w|-]+); PASSWD ([\\w|-|#|!]+);";
        pattern = Pattern.compile(sintaksa);
        regex = pattern.matcher(komande);
        if (regex.matches()) {
            if(baza.preuzmiIdKorisnika(regex.group(1))!=-1){
                odgovor = Konstante.error50;
            }else{
                boolean dodan = dodajKorisnika(regex.group(1), regex.group(2));
            if (dodan) {
                odgovor = "OK 10;";
            } else {
                odgovor = Konstante.error50;
            }
            saljiPoruku(komande);
            }
            return true;
        }
        return false;
    }

    private synchronized boolean dodajKorisnika(String korisnickoIme, String lozinka) {
        baza = new BazaKontroler(bp);
        int id = baza.preuzmiIdKorisnika(korisnickoIme);
        if(id!=-1){
            return false;
        }
        String insertUpit = "Insert into korisnici (korisnickoIme,lozinka,sredstva,TipKorisnika) values ('" + korisnickoIme + "', '" + lozinka + "',0,1)";
        baza.insertUpit(insertUpit);
        String drugiUpit = "Insert into korisnici_grupe(KorisnickoIme, Grupa) values('"+ korisnickoIme + "','korisnik')";
        return baza.insertUpit(drugiUpit);
        
    }

    /**
     * Metoda vraca String sa sadrzajem : OK; {trenutno vrijeme}.
     *
     * @return
     */
    private String obradiKorisnika() {
        Date trenutnoVrijeme = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY.MM.dd hh:mm:ss");
        return "OK; " + sdf.format(trenutnoVrijeme);
    }

    /**
     * Metoda evidentira zahtjeve koji se obrade u dretvi. Poziva se nakon
     * svakog obradjenog zahtjeva. Dodaje novi ZahtjevKorisnika u listu svoje
     * EvidencijeModel unutar evidencije, te mjenja brojace zahtjeva.
     *
     * @param odgovor
     * @param vrijemeZahtjeva
     * @param vrijemeObrade
     * @param primljeniZahtjev
     */
    private void evidentirajZahtjev(String ipAdresa, long vrijemeObrade, String primljeniZahtjev) {
        
        String upit ="";
        if(primljeniZahtjev.startsWith("USER ")){
            int idKorisnika = baza.preuzmiIdKorisnika(regex.group(1));
            upit = "Insert into dnevnik(IpAdresa, Korisnik, KorisnikId, Trajanje, Zahtjev, Odgovor) values('"+ ipAdresa+"','"+regex.group(1)+ "'," + idKorisnika +","+ vrijemeObrade + ",'" + primljeniZahtjev + "','"+odgovor +"')"; 
        }else{
            upit = "Insert into dnevnik(IpAdresa, Korisnik, KorisnikId, Trajanje, Zahtjev, Odgovor) values('"+ ipAdresa+"','Nepoznat',0,"+ vrijemeObrade + ",'" + primljeniZahtjev + "','"+odgovor +"')";
        }
        baza.insertUpit(upit);
    }

    /**
     * Metoda provjerava korisniku korisnicko ime i lozinku. Usporedjuje ih s
     * postojecima u bazi podataka.
     *
     * @return
     */
    private String provjeriKorisnika(String korisnickoIme, String lozinka) {
        baza = new BazaKontroler(bp);
        return baza.provjeriKorisnika(korisnickoIme, lozinka);
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    public StanjeDretve getStanje() {
        return stanje;
    }

    public void setStanje(StanjeDretve stanje) {
        this.stanje = stanje;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setServer(ServerSucelje server) {
        this.server = server;
    }

    public void setBp(BP_Konfiguracija bp) {
        this.bp = bp;
    }

    public boolean isPozoviStopiranjeServera() {
        return pozoviStopiranjeServera;
    }

    public void setPozoviStopiranjeServera(boolean pozoviStopiranjeServera) {
        this.pozoviStopiranjeServera = pozoviStopiranjeServera;
    }
    
    private Object saljiPoruku(String komanda) {

        Konfiguracija konfig = SlusacPokretac.getKonfig();
        String smtpHost = konfig.dajPostavku("emailPosluzitelj");

        try {
            // Create the JavaMail session
            java.util.Properties properties = System.getProperties();
            properties.put("mail.smtp.host", smtpHost);

            Session session = Session.getInstance(properties, null);

            // Construct the message
            MimeMessage message = new MimeMessage(session);

            // Set the from address
            Address fromAddress = new InternetAddress(konfig.dajPostavku("adresaZaSlanje"));
            message.setFrom(fromAddress);

            // Parse and set the recipient addresses
            Address[] toAddresses = InternetAddress.parse(konfig.dajPostavku("emailAdresa"));
            message.setRecipients(Message.RecipientType.TO, toAddresses);

            // Set the subject and text
            message.setSubject(konfig.dajPostavku("emailPredmet"));
            String tekst = komanda + "\n";
            Date vrijemeZahtjeva = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
            tekst += "Vrijeme primanja zahtjeva: " + sdf.format(vrijemeZahtjeva) + "\n";
            int brojKorisnika = baza.prebrojiKorisnike();
            tekst += "Broj korisnika: " + brojKorisnika + "\n";
            message.setText(tekst);

            Transport.send(message);

        } catch (AddressException e) {
            e.printStackTrace();
            return "ERROR";

        } catch (SendFailedException e) {
            e.printStackTrace();
            return "ERROR";

        } catch (MessagingException e) {
            e.printStackTrace();
            return "ERROR";
        }
        return "OK";
    } 

}
