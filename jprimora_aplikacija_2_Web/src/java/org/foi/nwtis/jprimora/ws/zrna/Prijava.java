/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class Prijava {
    @EJB
    private KorisniciFacade korisniciFacade;

    private String korisnickoIme;
    private String lozinka;
    private String korisnickoGreska;
    private String lozinkaGreska;
    /**
     * Creates a new instance of Prijava
     */
    public Prijava() {
    }
    
    public String prijaviKorisnika(){
        
        if(korisnickoIme.equals("")){
            korisnickoGreska="Unesite korisnicko ime.";
            return "LOGIN";
        }else if(lozinka.equals("")){
            lozinkaGreska="Unesite lozinku.";
            return "LOGIN";
        }
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        
        
        String tipKorisnika = korisniciFacade.provjeriKorisnika(korisnickoIme, lozinka);
        if(tipKorisnika.equals("NOUSER")){
            korisnickoGreska = "Neispravi podaci za prijavu";
            return "LOGIN";
        }
        else if(tipKorisnika.equals("admin")){
            sesija.setAttribute("korisnik", korisnickoIme);
            sesija.setAttribute("tipKorisnika", tipKorisnika);
            Korisnici ovaj = korisniciFacade.dajKorisnik(korisnickoIme);
            dodajUAktivne(ovaj);
            return "ADMIN";
        }else if(tipKorisnika.equals("korisnik")){
            sesija.setAttribute("korisnik", korisnickoIme);
            sesija.setAttribute("tipKorisnika", tipKorisnika);
            Korisnici ovaj = korisniciFacade.dajKorisnik(korisnickoIme);
            dodajUAktivne(ovaj);
            return "USER";
        }
        return "LOGIN";
    }
    
    private void dodajUAktivne(Korisnici korisnik){
        ServletContext servletContext = (ServletContext) FacesContext
        .getCurrentInstance().getExternalContext().getContext();
        List<Korisnici> aktivniKorisnici = (List<Korisnici>) servletContext.getAttribute("aktivniKorisnici");
        aktivniKorisnici.add(korisnik);
        servletContext.setAttribute("aktivniKorisnici", aktivniKorisnici);
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getKorisnickoGreska() {
        return korisnickoGreska;
    }

    public void setKorisnickoGreska(String korisnickoGreska) {
        this.korisnickoGreska = korisnickoGreska;
    }

    public String getLozinkaGreska() {
        return lozinkaGreska;
    }

    public void setLozinkaGreska(String lozinkaGreska) {
        this.lozinkaGreska = lozinkaGreska;
    }
    
}
