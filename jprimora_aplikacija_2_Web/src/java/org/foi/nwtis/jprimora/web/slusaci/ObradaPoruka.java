/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;
import org.foi.nwtis.jprimora.ejb.sb.PosiljateljJMS;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.jprimora.jms.JMSPoruka;

/**
 * Dretva koja radi u pozadini i u intervalima dohvaća poruke i obrađuje
 * naredbe.
 *
 * @author Josip
 */
public class ObradaPoruka extends Thread {
    KorisniciFacade korisniciFacade = lookupKorisniciFacadeBean();
    PosiljateljJMS posiljateljJMS = lookupPosiljateljJMSBean();

    

    private String server, korisnik, lozinka, direktorij;
    private Konfiguracija konfig;
    private BP_Konfiguracija bp;
    private int ukupanBrojPoruka = 0;
    private int brojPoruka, brojNWTiSPoruka;
    private List<Korisnici> dodaniKorisnici;
    private List<Korisnici> neuspjesniKorisnici;

    /**
     * Konstruktor koji prima konfiguracije
     *
     * @param konfig
     * @param bp
     */
    public ObradaPoruka(Konfiguracija konfig, BP_Konfiguracija bp) {
        this.konfig = konfig;
        this.bp = bp;
        this.server = konfig.dajPostavku("emailPosluzitelj");
        this.korisnik = konfig.dajPostavku("emailAdresa");
        this.lozinka = konfig.dajPostavku("emailLozinka");
    }

    public ObradaPoruka() {
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * U ovoj metodi dretva se spoji na server i dohvati poruke
     */
    @Override
    public void run() {
        while (true) {
            long pocetnoVrijeme = System.currentTimeMillis();
            brojPoruka = brojNWTiSPoruka = 0;

            long brojMilisekundiZaSpavanje = 0;
            try {
                Properties properties = System.getProperties();
                properties.put("mail.smtp.host", server);

                // Start the session
                Session session = Session.getInstance(properties, null);

                Store store = session.getStore("imap");
                store.connect(server, korisnik, lozinka);

                Folder inboxFolder = store.getFolder("INBOX");
                inboxFolder.open(Folder.READ_WRITE);

                String mapaNWTiSPoruke = konfig.dajPostavku("ispravnePoruke");
                if (!store.getFolder(mapaNWTiSPoruke).exists()) {
                    store.getFolder(mapaNWTiSPoruke).create(Folder.HOLDS_MESSAGES);
                }

                String mapaNeNWTiSPoruke = konfig.dajPostavku("ostalePoruke");
                if (!store.getFolder(mapaNeNWTiSPoruke).exists()) {
                    store.getFolder(mapaNeNWTiSPoruke).create(Folder.HOLDS_MESSAGES);
                }

                Folder ispravneFolder = store.getFolder(mapaNWTiSPoruke);
                ispravneFolder.open(Folder.READ_WRITE);
                Folder ostaleFolder = store.getFolder(mapaNeNWTiSPoruke);
                ostaleFolder.open(Folder.READ_WRITE);

                Message[] messages = inboxFolder.getMessages();
                ukupanBrojPoruka += messages.length;
                brojPoruka = messages.length;

                if (messages.length != 0) {
                    obradiPoruke(messages, ispravneFolder, ostaleFolder, inboxFolder);
                }
                String sentIme = konfig.dajPostavku("mapaZaSlanje");
                if (!store.getFolder(sentIme).exists()) {
                    store.getFolder(sentIme).create(Folder.HOLDS_MESSAGES);
                }

                Folder sentFolder = store.getFolder(sentIme);

                long intervalDretve = Long.parseLong(konfig.dajPostavku("intervalDretve")) * 1000;
                long zavrsnoVrijeme = System.currentTimeMillis();
                brojMilisekundiZaSpavanje = intervalDretve - (zavrsnoVrijeme - pocetnoVrijeme);
                inboxFolder.close(true);
                store.close();

                posaljiStatistiku(sentFolder, pocetnoVrijeme, zavrsnoVrijeme);

            } catch (NoSuchProviderException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException | IOException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                sleep(brojMilisekundiZaSpavanje);
            } catch (InterruptedException ex) {
                Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }

        }
    }

    /**
     * Metoda primi poruke za pojedini ciklus i pozove obradu onih koje treba.
     * Sve poruke raspodjeli u mape.
     *
     * @param messages
     * @param ispravneFolder
     * @param neispravneFolder
     * @param inbox
     * @throws MessagingException
     * @throws IOException
     */
    private void obradiPoruke(Message[] messages, Folder ispravneFolder, Folder neispravneFolder, Folder inbox) throws MessagingException, IOException {

        String NWTiS_predmet = konfig.dajPostavku("emailPredmet");
        ArrayList<Message> ispravnePorukeLista = new ArrayList<>();
        ArrayList<Message> neispravnePorukeLista = new ArrayList<>();
        for (int i = 0; i < messages.length; ++i) {
            MimeMessage message = (MimeMessage) messages[i];
            String tip = message.getContentType().toLowerCase();
            if (tip.startsWith("text/plain")) {
                String subject = message.getSubject();
                if (subject != null) {
                    if (message.getSubject().startsWith(NWTiS_predmet)) {
                        String sadrzaj = (String) message.getContent();
                        sadrzaj = sadrzaj.trim();
                        String[] linije = sadrzaj.split("\n");

                        Matcher matcher = provjeriSadrzaj(linije[0]);

                        if (matcher == null) {
                            kopirajPoruku(message, inbox, neispravneFolder);
                        } else {
                            kopirajPoruku(message, inbox, ispravneFolder);
                            String rezultat = obradiNaredbu(matcher);
                            brojNWTiSPoruka++;
                        }

                    } else {
                        kopirajPoruku(message, inbox, neispravneFolder);
                    }
                } else {
                    kopirajPoruku(message, inbox, neispravneFolder);
                }
            } else {
                kopirajPoruku(message, inbox, neispravneFolder);
            }
            message.setFlag(Flags.Flag.DELETED, true);
        }
    }

    /**
     * Metoda kopira poruku iz jedne mape u drugu.
     *
     * @param poruka
     * @param izvor
     * @param odrediste
     */
    private void kopirajPoruku(Message poruka, Folder izvor, Folder odrediste) {
        Message[] message = {poruka};
        try {
            izvor.copyMessages(message, odrediste);
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Metoda obrađuje pojedinu naredbu temeljenu na regexu ranije provjerenom.
     * @param m
     * @return
     */
    private String obradiNaredbu(Matcher m) {
        String user = m.group(1);
        String password = m.group(2);

        if (postojiKorisnik(user)) {
            return "ERROR Korisnik vec postoji.";
        } else {
            Korisnici noviKorisnik = new Korisnici();
            noviKorisnik.setKorisnickoime(m.group(1));
            noviKorisnik.setLozinka(m.group(2));

            korisniciFacade.create(noviKorisnik);
            return "OK 10;";
        }

    }

    /**
     * metoda provjerava korisnika u bazi podataka u tablici "Polaznici".
     * @param user
     * @param password
     * @return
     */
    private boolean postojiKorisnik(String user) {
        if (korisniciFacade.dajKorisnikId(user) == -1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Metoda provjerava sadrzaj poruke koja bi temeljem naslova trebala
     * sadrzavati naredbu.
     *
     * @param sadrzaj
     * @return
     */
    private Matcher provjeriSadrzaj(String sadrzaj) {
        String sintaksa = "^ADD ([\\w|-]+); PASSWD ([\\w|-|#|!]+);";

        Pattern pattern = Pattern.compile(sintaksa);
        Matcher m = pattern.matcher(sadrzaj);
        boolean status = m.matches();
        if (status) {
            int kraj = m.groupCount();
            for (int i = 0; i <= kraj; i++) {
                System.out.println(i + ". " + m.group(i));
            }
            return m;
        } else {
            System.out.println("Sadrzaj poruke ne odgovara!");
            return null;
        }
    }

    /**
     * Metoda salje poruku sa statistikom za upravo provedeni ciklus.
     *
     * @param sentFolder
     * @param pocetnoVrijeme
     * @param zavrsnoVrijeme
     * @param brojNovihPoruka
     */
    private void posaljiStatistiku(Folder sentFolder, long pocetnoVrijeme, long zavrsnoVrijeme) {
            
            Date vrijemePocetka = new Date(pocetnoVrijeme);
            Date vrijemeZavrsetka = new Date(zavrsnoVrijeme);
                        
            JMSPoruka poruka = new JMSPoruka(vrijemePocetka, vrijemeZavrsetka, brojPoruka, brojNWTiSPoruka, dodaniKorisnici, neuspjesniKorisnici);
            String milis =  String.valueOf(System.currentTimeMillis());
            poruka.setMilis(milis);
            posiljateljJMS.sendJMSMessageToNWTiS_jprimora_1(poruka);

            System.out.println("Poruka uspjesno poslana....");
    }

    @Override
    public synchronized void start() {
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    private PosiljateljJMS lookupPosiljateljJMSBean() {
        try {
            Context c = new InitialContext();
            return (PosiljateljJMS) c.lookup("java:global/jprimora_aplikacija_2/jprimora_aplikacija_2_EJB/PosiljateljJMS!org.foi.nwtis.jprimora.ejb.sb.PosiljateljJMS");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private KorisniciFacade lookupKorisniciFacadeBean() {
        try {
            Context c = new InitialContext();
            return (KorisniciFacade) c.lookup("java:global/jprimora_aplikacija_2/jprimora_aplikacija_2_EJB/KorisniciFacade!org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    

}
