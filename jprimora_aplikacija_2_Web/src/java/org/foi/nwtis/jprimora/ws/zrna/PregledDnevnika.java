/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.foi.nwtis.jprimora.ejb.eb.Dnevnik;
import org.foi.nwtis.jprimora.ejb.sb.DnevnikFacade;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class PregledDnevnika {

    private List<Dnevnik> dnevnik = new ArrayList<>();
    private Date datumOd, datumDo;
    private String ipAdresa, korisnickoIme;
    private int trajanjeOd, trajanjeDo, satiOd, satiDo, minuteOd, minuteDo;

    @EJB
    private DnevnikFacade dnevnikFacade;

    /**
     * Creates a new instance of PregledDnevnika
     */
    public PregledDnevnika() {

    }

    /**
     * Metoda preuzima zapise iz tablice Dnevnik temeljem unesenih filtera.
     */
    public void preuzmiDnevnik() {
        
        postaviDatume();

        dnevnik = dnevnikFacade.filterDnevnik(datumOd, datumDo, ipAdresa, trajanjeOd, trajanjeDo, korisnickoIme);

    }
    
    /**
     * Ukoliko su uneseni datumi u filtere, postavlja im sate i minute.
     */
    private void postaviDatume(){
        if (datumOd != null) {
            if (satiOd != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(datumOd);
                calendar.set(Calendar.HOUR_OF_DAY, satiOd);
                datumOd = calendar.getTime();
            }
            if (minuteOd != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(datumOd);
                calendar.set(Calendar.MINUTE, minuteOd);
                datumOd = calendar.getTime();
            }
        }
        if (datumDo != null) {
            if (satiDo != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(datumDo);
                calendar.set(Calendar.HOUR_OF_DAY, satiDo);
                datumDo = calendar.getTime();
            }
            if (minuteDo != 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(datumDo);
                calendar.set(Calendar.MINUTE, minuteDo);
                datumDo = calendar.getTime();
            }
        }
    }

    public List<Dnevnik> getDnevnik() {
        return dnevnik;
    }

    public void setDnevnik(List<Dnevnik> dnevnik) {
        this.dnevnik = dnevnik;
    }

    public Date getDatumOd() {
        return datumOd;
    }

    public void setDatumOd(Date datumOd) {
        this.datumOd = datumOd;
    }

    public Date getDatumDo() {
        return datumDo;
    }

    public void setDatumDo(Date datumDo) {
        this.datumDo = datumDo;
    }

    public String getIpAdresa() {
        return ipAdresa;
    }

    public void setIpAdresa(String ipAdresa) {
        this.ipAdresa = ipAdresa;
    }

    public int getTrajanjeOd() {
        return trajanjeOd;
    }

    public void setTrajanjeOd(int trajanjeOd) {
        this.trajanjeOd = trajanjeOd;
    }

    public int getTrajanjeDo() {
        return trajanjeDo;
    }

    public void setTrajanjeDo(int trajanjeDo) {
        this.trajanjeDo = trajanjeDo;
    }

    public int getSatiOd() {
        return satiOd;
    }

    public void setSatiOd(int satiOd) {
        this.satiOd = satiOd;
    }

    public int getSatiDo() {
        return satiDo;
    }

    public void setSatiDo(int satiDo) {
        this.satiDo = satiDo;
    }

    public int getMinuteOd() {
        return minuteOd;
    }

    public void setMinuteOd(int minuteOd) {
        this.minuteOd = minuteOd;
    }

    public int getMinuteDo() {
        return minuteDo;
    }

    public void setMinuteDo(int minuteDo) {
        this.minuteDo = minuteDo;
    }

    public String getKorisnickoIme() {
        return korisnickoIme;
    }

    public void setKorisnickoIme(String korisnickoIme) {
        this.korisnickoIme = korisnickoIme;
    }
    
}
