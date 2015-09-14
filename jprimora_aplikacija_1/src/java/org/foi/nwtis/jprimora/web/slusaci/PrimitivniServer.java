/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.jprimora.dodaci.Konstante;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;

/**
 * Objekt ove klase prestavlja serversku aplikaciju u sustavu. Klasa
 implementira sučelje ServerSucelje.
 *
 * @author Josip
 */
public class PrimitivniServer extends Thread implements ServerSucelje  {

    private Konfiguracija konfig;
    private BP_Konfiguracija bpKonfig;
    private Matcher mParametri;
    private int redniBrojZahtjeva = 0;
    private ThreadGroup threadGroup = new ThreadGroup("jprimora");
    protected boolean pauza = false;
    private boolean stopiran = false;
    private int brojDretvi;
    private ServerSocket ss;
    private PozadinskaDretva pozadinska;

    @Override
    public void run() {
        pokreniServer();
    }

    public void setBrojDretvi(int brojDretvi) {
        this.brojDretvi = brojDretvi;
    }

    /**
     * Konstruktor primitivnog servera koji prima objekt konfiguracije
     *
     * @param konfig
     * @param bpKonfig
     */
    public PrimitivniServer(Konfiguracija konfig, BP_Konfiguracija bpKonfig) {
        this.konfig = konfig;
        this.bpKonfig = bpKonfig;
    }

    /**
     * Metoda pokrece server.Metoda takoder kreira potrebne dretve za obradu
     * zahtjeva, te ih poziva.
     */
    public void pokreniServer() {
        brojDretvi = Integer.parseInt(konfig.dajPostavku("brojDretvi"));

        pozadinska = new PozadinskaDretva(konfig, bpKonfig);
        pozadinska.start();

        DretvaObrada[] dretve = new DretvaObrada[brojDretvi];
        for (int i = 0; i < brojDretvi; i++) {
            dretve[i] = new DretvaObrada(threadGroup, "jprimora_" + i);
            dretve[i].setKonfig(konfig);
            dretve[i].setBp(bpKonfig);
            dretve[i].setServer(this);
        }

        int port = Integer.parseInt(konfig.dajPostavku("port"));

        try {
            ss = new ServerSocket(port);
            while (true) {
                Socket socket = ss.accept();
                DretvaObrada slobodnaDretva = dajSlobodnuDretvu(dretve);
                redniBrojZahtjeva++;
                if (redniBrojZahtjeva == brojDretvi) {
                    redniBrojZahtjeva = 0;
                }
                if (slobodnaDretva == null) {
                    nemaSlobodnihDretvi(socket);
                } else {
                    slobodnaDretva.setStanje(DretvaObrada.StanjeDretve.Zauzeta);
                    slobodnaDretva.setSocket(socket);
                    if (slobodnaDretva.getState() == Thread.State.NEW) {
                        slobodnaDretva.start();
                    } else if (slobodnaDretva.getState() == Thread.State.WAITING) {
                        slobodnaDretva.prekiniCekanje();
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PrimitivniServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Metoda kruzno dodjeljuje slobodne dretve u sustavu. Ukoliko niti jedna
     * nije slobodna, vraca null.
     *
     * @param dretve
     * @return
     */
    private DretvaObrada dajSlobodnuDretvu(DretvaObrada[] dretve) {
        DretvaObrada slobodnaDretva = null;

        if (dretve[redniBrojZahtjeva].getStanje() == DretvaObrada.StanjeDretve.Slobodna) {
            slobodnaDretva = dretve[redniBrojZahtjeva];
        } else {
            for (int i = 0; i < dretve.length; i++) {
                if (dretve[i].getStanje() == DretvaObrada.StanjeDretve.Slobodna) {
                    slobodnaDretva = dretve[i];
                    break;
                }
            }
        }
        for (int i = 0; i < dretve.length; i++) {
            if (dretve[i].getStanje() == DretvaObrada.StanjeDretve.Zauzeta) {
                System.out.println("Zauzeta dretva: " + dretve[i].getName());
            }
        }
        return slobodnaDretva;
    }

    /**
     * Metoda se pozuva ukoliko nema slobodnih dretvi. Klijentu putem izlaznog
     * toka soketa vraca odgovor s greskom.
     *
     * @param socket
     */
    private void nemaSlobodnihDretvi(Socket socket) {
        OutputStream output = null;
        try {
            output = socket.getOutputStream();
            System.out.println("Slanje klijentu ERROR 80");
            output.write(Konstante.error80.getBytes());
            output.flush();
            socket.shutdownOutput();
        } catch (IOException ex) {
            Logger.getLogger(DretvaObrada.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public boolean isPauza() {
        return pauza;
    }

    @Override
    public void setPauza(boolean pauza) {
        this.pauza = pauza;
        pozadinska.setPauza(pauza);
    }
    
    @Override
    public void startaj() {
        this.pauza = false;
        pozadinska.setPauza(false);
        this.stopiran = false;
    }

    @Override
    public void stopiraj() {
        this.pauza = true;
        pozadinska.setPauza(true);
        this.stopiran = true;
    }

    @Override
    public int getBrojDretvi() {
        return brojDretvi;
    }

    public Konfiguracija getKonfig() {
        return konfig;
    }

    public void setKonfig(Konfiguracija konfig) {
        this.konfig = konfig;
    }

    public BP_Konfiguracija getBpKonfig() {
        return bpKonfig;
    }

    public void setBpKonfig(BP_Konfiguracija bpKonfig) {
        this.bpKonfig = bpKonfig;
    }

    public boolean isStopiran() {
        return stopiran;
    }

    public void setStopiran(boolean stopiran) {
        this.stopiran = stopiran;
    }

    
    
}
