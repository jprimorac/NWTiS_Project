/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
public class AzuriranjePodataka {
    @EJB
    private KorisniciFacade korisniciFacade;

    private Korisnici korisnik;
    
    /**
     * Creates a new instance of AzuriranjePodataka
     */
    public AzuriranjePodataka() {
        
    }
    
    @PostConstruct
    private void preuzmiKorisnika(){
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        String koriscnickoIme = (String) sesija.getAttribute("korisnik");
        korisnik = korisniciFacade.dajKorisnik(koriscnickoIme);
    }
    
    public String azurirajKorisnika(){
        korisniciFacade.updateKorisnik(korisnik);
        return "OK";
    }

    public Korisnici getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(Korisnici korisnik) {
        this.korisnik = korisnik;
    }
    
    
    
}
