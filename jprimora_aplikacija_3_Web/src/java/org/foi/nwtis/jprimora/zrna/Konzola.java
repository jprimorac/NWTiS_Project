/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.zrna;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.jprimora.ejb.eb.Adresa;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.jms.CuvarPoruka;
import org.foi.nwtis.jprimora.jms.JMSPoruka;
import org.foi.nwtis.jprimora.jms.JMSPoruka2;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class Konzola {

    @EJB
    private CuvarPoruka cuvarPoruka;

    private KonzolaHelper helper;
    private List<JMSPoruka> poruke1;
    private List<JMSPoruka2> poruke2;
    private List<Korisnici> aktivniKorisnici;
    private List<Adresa> adreseKorisnika;
    private String vrijemePoruke;
    private JMSPoruka prvaPoruka;
    private JMSPoruka2 drugaPoruka;
    private Korisnici noviKorisnik;
    private String trenutnaPrognoza;
    private Korisnici odabraniKorisnik;
    private String noviKorisnicko;
    private String noviLozinka;

    private boolean otvoreneJMSPoruke;
    private boolean otvorenaJednaJMS;
    private boolean otvorenoDodavanjeKorisnika;
    private boolean otvorenoAktivniKorisnici;
    private boolean otvorenoAdrese;
    private boolean otvorenoTrenutnoPrognoza;
    private boolean otvorenaKoznola;
    private boolean otvorenaPrva;
    private boolean otvorenaDruga;

    /**
     * Creates a new instance of Konzola
     */
    public Konzola() {
        helper = new KonzolaHelper();
    }

    public void otvoriJMSPoruke() {
        otvoreneJMSPoruke = true;
        otvorenaJednaJMS = false;
        otvorenoDodavanjeKorisnika = false;
        otvorenoAktivniKorisnici = false;
        otvorenoAdrese = false;
        otvorenoTrenutnoPrognoza = false;
        otvorenaKoznola = false;

        poruke1 = cuvarPoruka.getPoruke1();
        poruke2 = cuvarPoruka.getPoruke2();

    }

    public void otvoriJednuJMS() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String milis = params.get("milis");

        otvoreneJMSPoruke = false;
        otvorenaJednaJMS = true;
        otvorenoDodavanjeKorisnika = false;
        otvorenoAktivniKorisnici = false;
        otvorenoAdrese = false;
        otvorenoTrenutnoPrognoza = false;
        otvorenaKoznola = false;

        prvaPoruka = null;
        drugaPoruka = null;
        otvorenaPrva = false;
        otvorenaDruga = false;

        for (JMSPoruka zaOtvorit : poruke1) {
            if (zaOtvorit.getMilis().equals(milis)) {
                prvaPoruka = zaOtvorit;
            }
        }
        for (JMSPoruka2 zaOtvorit : poruke2) {
            if (zaOtvorit.getMilis().equals(milis)) {
                drugaPoruka = zaOtvorit;
            }
        }
        if (prvaPoruka != null) {
            otvorenaPrva = true;
        }
        if (drugaPoruka != null) {
            otvorenaDruga = true;
        }

    }

    public void izbrisiJMS() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String milis = params.get("milis");

        JMSPoruka zaBrisat = new JMSPoruka();

        for (JMSPoruka j : poruke1) {
            if (j.getMilis().equals(milis)) {
                zaBrisat = j;
            }
        }
        if (zaBrisat != null) {
            poruke1.remove(zaBrisat);
            cuvarPoruka.setPoruke1(poruke1);
        }

        JMSPoruka2 zaBrisat2 = new JMSPoruka2();
        for (JMSPoruka2 l : poruke2) {
            if (l.getMilis().equals(milis)) {
                zaBrisat2 = l;
            }
        }
        if (zaBrisat != null) {
            poruke2.remove(zaBrisat2);
            cuvarPoruka.setPoruke2(poruke2);
        }
    }

    public void izbrisiSveJMS() {
        poruke1.clear();
        cuvarPoruka.setPoruke1(poruke1);
        poruke2.clear();
        cuvarPoruka.setPoruke2(poruke2);
    }

    public void otvoriDodavanjeKorisnika() {
        otvoreneJMSPoruke = false;
        otvorenaJednaJMS = false;
        otvorenoDodavanjeKorisnika = true;
        otvorenoAktivniKorisnici = false;
        otvorenoAdrese = false;
        otvorenoTrenutnoPrognoza = false;
        otvorenaKoznola = false;
    }

    public void dodajKorisnika() {
        helper.dodajKorisnika(noviKorisnicko, noviLozinka);
    }

    public void otvoriAktivneKorisnike() {
        otvoreneJMSPoruke = false;
        otvorenaJednaJMS = false;
        otvorenoDodavanjeKorisnika = false;
        otvorenoAktivniKorisnici = true;
        otvorenoAdrese = false;
        otvorenoTrenutnoPrognoza = false;
        otvorenaKoznola = false;

        aktivniKorisnici = helper.vratiAktivneKorisnike();
    }

    public void otvoriAdrese() {
        otvorenoAdrese = true;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String korisnickoIme = params.get("korisnickoIme");
        for (Korisnici k : aktivniKorisnici) {
            if (k.getKorisnickoime().equals(korisnickoIme)) {
                odabraniKorisnik = k;
                int idKorisnik = k.getIdkorisnik();
                adreseKorisnika = helper.dohvatiAdreseKorisnika(idKorisnik);
            }
        }
    }

    public void zatvoriAdrese() {
        otvorenoAdrese = false;
    }

    public void otvoriTrenutnoPrognoza() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String adresa = params.get("adresa");
        trenutnaPrognoza = helper.dohvatiPrognozu(odabraniKorisnik.getKorisnickoime(), odabraniKorisnik.getLozinka(), adresa);
        otvorenoTrenutnoPrognoza = true;
    }

    public void zatvoriTrenutnoPrognoza() {
        otvorenoTrenutnoPrognoza = false;
    }

    public void otvoriKonzolu() {
        otvoreneJMSPoruke = false;
        otvorenaJednaJMS = false;
        otvorenoDodavanjeKorisnika = false;
        otvorenoAktivniKorisnici = false;
        otvorenoAdrese = false;
        otvorenoTrenutnoPrognoza = false;
        otvorenaKoznola = true;
    }

    public void zatvoriJednuJMS() {
        otvorenaJednaJMS = false;
        otvoreneJMSPoruke = true;
    }

    public String handleCommand(String command, String[] params) {

        String naredba = command + " ";
        for (int i = 0; i < params.length; i++) {
            if (i != params.length - 1) {
                naredba += params[i] + " ";
            }else{
                naredba +=params[i];
            }
        }

        return helper.obradiNaredbu(naredba);
    }
    
    public void osvjeziJMS(){
        poruke1 = cuvarPoruka.getPoruke1();
        poruke2 = cuvarPoruka.getPoruke2();
    }

    // <editor-fold defaultstate="collapsed" desc="Getters">
    public boolean isOtvoreneJMSPoruke() {
        return otvoreneJMSPoruke;
    }

    public boolean isOtvorenaJednaJMS() {
        return otvorenaJednaJMS;
    }

    public boolean isOtvorenoDodavanjeKorisnika() {
        return otvorenoDodavanjeKorisnika;
    }

    public boolean isOtvorenoAktivniKorisnici() {
        return otvorenoAktivniKorisnici;
    }

    public boolean isOtvorenoAdrese() {
        return otvorenoAdrese;
    }

    public boolean isOtvorenoTrenutnoPrognoza() {
        return otvorenoTrenutnoPrognoza;
    }

    public boolean isOtvorenaKoznola() {
        return otvorenaKoznola;
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

    public List<Korisnici> getAktivniKorisnici() {
        return aktivniKorisnici;
    }

    public void setAktivniKorisnici(List<Korisnici> aktivniKorisnici) {
        this.aktivniKorisnici = aktivniKorisnici;
    }

    public JMSPoruka getPrvaPoruka() {
        return prvaPoruka;
    }

    public JMSPoruka2 getDrugaPoruka() {
        return drugaPoruka;
    }

    public boolean isOtvorenaPrva() {
        return otvorenaPrva;
    }

    public void setOtvorenaPrva(boolean otvorenaPrva) {
        this.otvorenaPrva = otvorenaPrva;
    }

    public boolean isOtvorenaDruga() {
        return otvorenaDruga;
    }

    public void setOtvorenaDruga(boolean otvorenaDruga) {
        this.otvorenaDruga = otvorenaDruga;
    }

    public Korisnici getNoviKorisnik() {
        return noviKorisnik;
    }

    public void setNoviKorisnik(Korisnici noviKorisnik) {
        this.noviKorisnik = noviKorisnik;
    }

    public List<Adresa> getAdreseKorisnika() {
        return adreseKorisnika;
    }

    public void setAdreseKorisnika(List<Adresa> adreseKorisnika) {
        this.adreseKorisnika = adreseKorisnika;
    }

    public String getTrenutnaPrognoza() {
        return trenutnaPrognoza;
    }

    public void setTrenutnaPrognoza(String trenutnaPrognoza) {
        this.trenutnaPrognoza = trenutnaPrognoza;
    }
    
    public String getNoviKorisnicko() {
        return noviKorisnicko;
    }

    public void setNoviKorisnicko(String noviKorisnicko) {
        this.noviKorisnicko = noviKorisnicko;
    }

    public String getNoviLozinka() {
        return noviLozinka;
    }

    public void setNoviLozinka(String noviLozinka) {
        this.noviLozinka = noviLozinka;
    }

 // </editor-fold>

    
}
