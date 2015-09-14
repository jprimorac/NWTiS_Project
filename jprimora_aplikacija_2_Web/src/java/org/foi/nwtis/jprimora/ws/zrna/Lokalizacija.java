/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class Lokalizacija  implements Serializable{
    
    private static Map<String, Object> jezici;
    private String originalURL;

    public Map<String, Object> getJezici() {
        return jezici;
    }

    private String odabraniJezik;
    private Locale vazecaLocalizacija;
    
    
    
    static {
        jezici = new HashMap<>();
        jezici.put("Hrvatski", new Locale("hr"));
        jezici.put("English", Locale.ENGLISH);
        jezici.put("Deutsch", Locale.GERMAN);
    }

    /**
     * Creates a new instance of Lokalizacija
     */
    public Lokalizacija() {
        //vazecaLocalizacija = FacesContext.getCurrentInstance().getViewRoot().getLocale(); 
        //odabraniJezik = vazecaLocalizacija.getLanguage();
    }

    public String getOdabraniJezik() {
        return odabraniJezik;
    }

    public void setOdabraniJezik(String odabraniJezik) {
        this.odabraniJezik = odabraniJezik;
    }

    public Locale getVazecaLocalizacija() {
        return vazecaLocalizacija;
    }

    public void setVazecaLocalizacija(Locale vazecaLocalizacija) {
        this.vazecaLocalizacija = vazecaLocalizacija;
    }
    
    public void odaberiJezik(){
        Iterator i = jezici.keySet().iterator();
        while(i.hasNext()){
            String kljuc = (String) i.next();
            Locale l = (Locale) jezici.get(kljuc);
            if(odabraniJezik.equals(l.getLanguage())){
                try {
                    FacesContext.getCurrentInstance().getViewRoot().setLocale(l);
                    vazecaLocalizacija = l;
                    dohvatiUrl();
                    FacesContext.getCurrentInstance().getExternalContext().redirect(originalURL);
                    
                } catch (IOException ex) {
                    Logger.getLogger(Lokalizacija.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
       
    }
    
    private void dohvatiUrl(){
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        originalURL = (String) sesija.getAttribute("zadnjiURL");
    }

    public String getOriginalURL() {
        return originalURL;
    }

    public void setOriginalURL(String originalURL) {
        this.originalURL = originalURL;
    }
    
}
