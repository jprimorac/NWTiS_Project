/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.foi.nwtis.jprimora.podaci.Poruka;

/**
 *
 * @author Josip
 */
@ManagedBean
@RequestScoped
public class CitanjePoruke {

    /**
     * Creates a new instance of CitanjePoruke
     */
    public CitanjePoruke() {
    }
    
    private Poruka poruka;

    /**
     * Creates a new instance of PregledPoruke
     */
    @PostConstruct
    public void PregledPoruke() {
        PregledPoruka pregledPoruka = (PregledPoruka) 
                FacesContext.getCurrentInstance()
                        .getExternalContext()
                        .getSessionMap()
                        .get("pregledPoruka");
        
        poruka = pregledPoruka.getOtvorenaPoruka();
    }
    
    public String povratakPregledPoruka() {
        return "OK";
    }

    public Poruka getPoruka() {
        return poruka;
    }

    public void setPoruka(Poruka poruka) {
        this.poruka = poruka;
    }
    
}
