/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
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
@RequestScoped
public class Odjava {
    @EJB
    private KorisniciFacade korisniciFacade;

    /**
     * Creates a new instance of Odjava
     */
    public Odjava() {
    }

    public String odjaviKorisnika() {
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        String korisnickoIme = (String) sesija.getAttribute("korisnik");
        sesija.invalidate();
        
        Korisnici ovaj = korisniciFacade.dajKorisnik(korisnickoIme);
        oduzmiIzAktivnih(ovaj);

        String url = "/jprimora_aplikacija_2_Web/faces/index.xhtml";
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        try {
            ec.redirect(url);
        } catch (IOException ex) {
            Logger.getLogger(Odjava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "OK";
    }
    
    private void oduzmiIzAktivnih(Korisnici korisnik){
        ServletContext servletContext = (ServletContext) FacesContext
        .getCurrentInstance().getExternalContext().getContext();
        List<Korisnici> aktivniKorisnici = (List<Korisnici>) servletContext.getAttribute("aktivniKorisnici");
        aktivniKorisnici.remove(korisnik);
        servletContext.setAttribute("aktivniKorisnici", aktivniKorisnici);
    }

}
