/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class PregledKorisnika {
    @EJB
    private KorisniciFacade korisniciFacade;

    private List<Korisnici> korisnici;
    private Korisnici odabraniKorisnik;
    /**
     * Creates a new instance of PregledKorisnika
     */
    public PregledKorisnika() {
    }
    
    @PostConstruct
    public void dohvatiKorisnike(){
        korisnici = korisniciFacade.findAll();
    }

    public List<Korisnici> getKorisnici() {
        return korisnici;
    }

    public void setKorisnici(List<Korisnici> korisnici) {
        this.korisnici = korisnici;
    }
    
    public String azurirajKorisnika(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        String korisnickoIme = params.get("odabrani");

        if (korisnickoIme == null) {
            return "ERROR";
        }

        boolean nadjena = false;
        for (int i = 0; i < korisnici.size() && !nadjena; i++) {
            if (korisnici.get(i).getKorisnickoime().equals(korisnickoIme)) {
                odabraniKorisnik = korisnici.get(i);
                nadjena = true;
            }
        }
        if (!nadjena) {
            return "NOT_OK";
        }
        
        return "OK";
        
    }    

    public Korisnici getOdabraniKorisnik() {
        return odabraniKorisnik;
    }

    public void setOdabraniKorisnik(Korisnici odabraniKorisnik) {
        this.odabraniKorisnik = odabraniKorisnik;
    }

}
