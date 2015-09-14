/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.podaci.Poruka;
import org.foi.nwtis.jprimora.web.slusaci.ObradaPoruka;
import org.foi.nwtis.jprimora.web.slusaci.SlusacPokretac;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class PregledPoruka {

    private String server, korisnik, lozinka;
    private List<Poruka> poruke;
    private Map<String, String> mape;
    private String odabranaMapa = "INBOX";
    private int brojPorukaZaPrikaz;
    private Poruka otvorenaPoruka;
    private Message[] messages;

    /**
     * Creates a new instance of PregledPoruka
     */
    public PregledPoruka() {
    }

    @PostConstruct
    public void dohvatiMape() {
        Konfiguracija konfig = SlusacPokretac.getKonfig();
        brojPorukaZaPrikaz = Integer.parseInt(konfig.dajPostavku("brojPoruka"));
        server = konfig.dajPostavku("emailPosluzitelj");
        korisnik = konfig.dajPostavku("emailAdresa");
        lozinka = konfig.dajPostavku("emailLozinka");

        try {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);

            // Start the session
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(server, korisnik, lozinka);

            Folder osnovnaMapa = store.getDefaultFolder();
            Folder[] folderi = osnovnaMapa.list();

            mape = new HashMap<>();
            for (Folder f : folderi) {
                mape.put(f.getName(), f.getName());
            }
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String odaberiMapu() {
        dohvatiPoruke();
        return "";
    }

    private void dohvatiPoruke() {
        try {
            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);

            // Start the session
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(server, korisnik, lozinka);

            //TODO preuzmi iz postavki
            // Open the INBOX folder
            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_ONLY);

            messages = folder.getMessages();
            poruke = new ArrayList<>();

            // Print each message
            for (int i = 0; i < messages.length; ++i) {
                MimeMessage m = (MimeMessage) messages[i];
                String tip = m.getContentType().toLowerCase();
                Poruka p = new Poruka(m.getHeader("Message-ID")[0], m.getContent().toString(), m.getReceivedDate(), m.getFrom()[0].toString(), m.getSubject(), m.getContentType(), m.getSize(), 0, m.getFlags(), false, true);
                poruke.add(p);
            }
            store.close();
        } catch (MessagingException ex) {
            Logger.getLogger(ObradaPoruka.class.getName()).log(Level.SEVERE, null, ex);

        } catch (IOException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(odabranaMapa);
    }

    public String otvoriPoruku() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String idPoruke = params.get("idPoruke");

        if (idPoruke == null) {
            return "ERROR";
        }

        boolean nadjena = false;
        for (int i = 0; i < poruke.size() && !nadjena; i++) {
            if (poruke.get(i).getId().equals(idPoruke)) {
                otvorenaPoruka = poruke.get(i);
                nadjena = true;
            }
        }

        if (!nadjena) {
            return "NOT_OK";
        }
        return "OK";
    }

    public String izbrisiPoruku() {

        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
            String idPoruke = params.get("idPoruke");

            if (idPoruke == null) {
                return "ERROR";
            }

            Properties properties = System.getProperties();
            properties.put("mail.smtp.host", server);

            // Start the session
            Session session = Session.getInstance(properties, null);

            // Connect to the store
            Store store = session.getStore("imap");
            store.connect(server, korisnik, lozinka);

            //TODO preuzmi iz postavki
            // Open the INBOX folder
            Folder folder = store.getFolder(odabranaMapa);
            folder.open(Folder.READ_WRITE);

            messages = folder.getMessages();

            boolean nadjena = false;
            // Print each message
            for (int i = 0; i < messages.length; i++) {
                if (messages[i].getHeader("Message-ID")[0].equals(idPoruke)) {
                    messages[i].setFlag(Flags.Flag.DELETED, true);
                    nadjena = true;
                }

            }

            if (!nadjena) {
                return "NOT_OK";
            }
            folder.close(true);
            store.close();
            dohvatiPoruke();
            return "OK";
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(PregledPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }

    public List<Poruka> getPoruke() {
        return poruke;
    }

    public void setPoruke(List<Poruka> poruke) {
        this.poruke = poruke;
    }

    public Map<String, String> getMape() {
        return mape;
    }

    public void setMape(Map<String, String> mape) {
        this.mape = mape;
    }

    public String getOdabranaMapa() {
        return odabranaMapa;
    }

    public void setOdabranaMapa(String odabranaMapa) {
        this.odabranaMapa = odabranaMapa;
    }

    public Poruka getOtvorenaPoruka() {
        return otvorenaPoruka;
    }

    public int getBrojPorukaZaPrikaz() {
        return brojPorukaZaPrikaz;
    }

}
