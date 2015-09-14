/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.slusaci;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.jprimora.jms.CuvarPoruka;
import org.foi.nwtis.jprimora.jms.JMSPoruka;
import org.foi.nwtis.jprimora.jms.JMSPoruka2;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.jprimora.konfiguracije.NemaKonfiguracije;

/**
 * Web application lifecycle listener.
 *
 * @author Josip
 */
public class SlusacAplikacje implements ServletContextListener {
    CuvarPoruka cuvarPoruka = lookupCuvarPorukaBean();
    
    
    
    private static Konfiguracija konfig;
    private List<JMSPoruka> poruke1;
    private List<JMSPoruka2> poruke2;
    private String imeDatoteka1;
    private String imeDatoteka2;

    
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
    String direktorij = sce.getServletContext().getRealPath("/WEB-INF")
                + java.io.File.separator;

        String datoteka = direktorij + 
                sce.getServletContext().getInitParameter("konfig");

        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            sce.getServletContext().setAttribute("konfig", konfig);
            
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);
        }
        konfig.spremiPostavku("direktorij", direktorij);
        cuvarPoruka.setKonfig(konfig);
        poslijeKonstruktora();
        
    }
    
    public void poslijeKonstruktora() {
        
        imeDatoteka1 = "NWTIS_poruke/" + konfig.dajPostavku("datoteka1");
        imeDatoteka2 = "NWTIS_poruke/" + konfig.dajPostavku("datoteka2");

        File datoteka1 = new File(imeDatoteka1);
        if (!datoteka1.exists()) {
            System.out.println("Ne postoji datoteka s serijaliziranom JMS1.");
            poruke1 = new ArrayList<>();
        } else {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(datoteka1));
                poruke1 = (List<JMSPoruka>) objectInputStream.readObject();

            } catch (FileNotFoundException ex) {
                Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        cuvarPoruka.setPoruke1(poruke1);

        File datoteka2 = new File(imeDatoteka2);
        if (!datoteka2.exists()) {
            System.out.println("Ne postoji datoteka s serijaliziranom JMS2.");
            poruke2 = new ArrayList<>();
        } else {
            try {
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(datoteka2));
                poruke2 = (List<JMSPoruka2>) objectInputStream.readObject();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        cuvarPoruka.setPoruke2(poruke2);
    }
 
    public static Konfiguracija dajKonfig(){
        return konfig;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        dohvatiISpremi();
    }
    
    public void dohvatiISpremi() {
        poruke1 = cuvarPoruka.getPoruke1();
        poruke2 = cuvarPoruka.getPoruke2();

        File datoteka = new File(imeDatoteka1);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(datoteka));
            objectOutputStream.writeObject(poruke1);
            objectOutputStream.close();

        } catch (IOException ex) {
            Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);

        }

        datoteka = new File(imeDatoteka2);
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(datoteka));
            objectOutputStream.writeObject(poruke2);
            objectOutputStream.close();

        } catch (IOException ex) {
            Logger.getLogger(SlusacAplikacje.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private CuvarPoruka lookupCuvarPorukaBean() {
        try {
            Context c = new InitialContext();
            return (CuvarPoruka) c.lookup("java:global/jprimora_aplikacija_3/jprimora_aplikacija_3_EJB/CuvarPoruka!org.foi.nwtis.jprimora.jms.CuvarPoruka");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

   


    
}
