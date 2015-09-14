/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ws.zrna;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceRef;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;
import org.foi.nwtis.jprimora.ejb.sb.PosiljateljJMS;
import org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP_Service;
import org.foi.nwtis.jprimora.klijenti.SoapListaAdresa;
import org.foi.nwtis.jprimora.klijenti.Adresa;
import org.foi.nwtis.jprimora.klijenti.SoapJedanMeteo;
import org.foi.nwtis.jprimora.klijenti.SoapListaMeteo;
import org.foi.nwtis.jprimora.klijenti.MeteoPodaci;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.jms.JMSPoruka2;
import org.foi.nwtis.jprimora.web.slusaci.SlusacPokretac;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 *
 * @author Josip
 */
@ManagedBean
@SessionScoped
public class RadSAdresama {

    @EJB
    private PosiljateljJMS posiljateljJMS;
    @WebServiceRef(wsdlLocation = "WEB-INF/wsdl/localhost_8084/jprimora_aplikacija_1/GeoMeteoSOAP.wsdl")
    private GeoMeteoSOAP_Service service;
    @EJB
    private KorisniciFacade korisniciFacade;

    private Korisnici korisnik;
    private boolean otkrijAdrese = false;
    private boolean otkrijMojeAdrese = false;
    private boolean otkrijZadnjiPodaci = false;
    private boolean otkrijDodavanjeAdrese = false;
    private List<Adresa> sveAdrese;
    private List<Adresa> mojeAdrese;
    private String novaAdresa;
    private List<MeteoPodaci> zadnjiMeteo;
    private List<String> sveAdresePopis;
    private String odabranaAdresa;
    private String centar;
    private MapModel model;

    /**
     * Creates a new instance of RadSAdresama
     */
    public RadSAdresama() {
    }

    @PostConstruct
    private void dohvatiKorisnika() {
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        HttpSession sesija = request.getSession();
        String korisnickoIme = (String) sesija.getAttribute("korisnik");
        korisnik = korisniciFacade.dajKorisnik(korisnickoIme);

        model = new DefaultMapModel();
    }

    public void otvoriSveAdrese() {
        SoapListaAdresa soapOdgovor = rangListaAdresa(korisnik.getKorisnickoime(), korisnik.getLozinka(), 1000);
        sveAdrese = soapOdgovor.getAdrese();
        sveAdresePopis = new ArrayList<>();
        for (Adresa a : sveAdrese) {
            sveAdresePopis.add(a.getAdresa());
        }

        otkrijAdrese = true;
    }

    public void dohvatiMeteo() {
        SoapJedanMeteo soapOdgovor = trenutniMeteoZaAdresu(odabranaAdresa, korisnik.getKorisnickoime(), korisnik.getLozinka());
        zadnjiMeteo = new ArrayList<>();
        zadnjiMeteo.add(soapOdgovor.getMeteo());
    }

    public void zatvoriSveAdrese() {
        otkrijAdrese = false;
    }

    public void otvoriMojeAdrese() {
        SoapListaAdresa soapOdgovor = listaDodanihAdresa(korisnik.getKorisnickoime(), korisnik.getLozinka());
        mojeAdrese = soapOdgovor.getAdrese();
        
        if (!mojeAdrese.isEmpty()) {
            centar = mojeAdrese.get(0).getGeoloc().getLatitude()+","+mojeAdrese.get(0).getGeoloc().getLongitude();
            for (Adresa a : mojeAdrese) {
                String latitude = a.getGeoloc().getLatitude();
                //latitude = latitude.replace('.', ',');
                double lat = Double.valueOf(latitude);
                String longitude = a.getGeoloc().getLongitude();
                //longitude = longitude.replace('.', ',');
                double lon = Double.valueOf(longitude);
                Marker marker = new Marker(new LatLng(lat, lon), a.getAdresa());
                model.addOverlay(marker);
            }
        }

        otkrijMojeAdrese = true;
    }

    public void zatvoriMojeAdrese() {
        otkrijMojeAdrese = false;
    }

    public void otvoriDodavanje() {
        otkrijDodavanjeAdrese = true;
    }

    public void zatvoriDodavanje() {
        otkrijDodavanjeAdrese = false;
    }

    public void otvoriPrognozu() {
        otkrijZadnjiPodaci = true;
    }

    public void zatvoriPrognozu() {
        otkrijZadnjiPodaci = false;
    }

    public void dodajAdresu() {

        JMSPoruka2 poruka = new JMSPoruka2(korisnik.getKorisnickoime(), korisnik.getLozinka(), novaAdresa);
        Date vrijeme = new Date();
        poruka.setVrijeme(vrijeme);
        String milis = String.valueOf(System.currentTimeMillis());
        poruka.setMilis(milis);
        posiljateljJMS.sendJMSMessageToNWTiS_jprimora_2(poruka);

    }

    public boolean isOtkrijAdrese() {
        return otkrijAdrese;
    }

    public void setOtkrijAdrese(boolean otkrijAdrese) {
        this.otkrijAdrese = otkrijAdrese;
    }

    public boolean isOtkrijMojeAdrese() {
        return otkrijMojeAdrese;
    }

    public void setOtkrijMojeAdrese(boolean otkrijMojeAdrese) {
        this.otkrijMojeAdrese = otkrijMojeAdrese;
    }

    public boolean isOtkrijZadnjiPodaci() {
        return otkrijZadnjiPodaci;
    }

    public void setOtkrijZadnjiPodaci(boolean otkrijZadnjiPodaci) {
        this.otkrijZadnjiPodaci = otkrijZadnjiPodaci;
    }

    public boolean isOtkrijDodavanjeAdrese() {
        return otkrijDodavanjeAdrese;
    }

    public void setOtkrijDodavanjeAdrese(boolean otkrijDodavanjeAdrese) {
        this.otkrijDodavanjeAdrese = otkrijDodavanjeAdrese;
    }

    public List<Adresa> getSveAdrese() {
        return sveAdrese;
    }

    public void setSveAdrese(List<Adresa> sveAdrese) {
        this.sveAdrese = sveAdrese;
    }

    public List<Adresa> getMojeAdrese() {
        return mojeAdrese;
    }

    public void setMojeAdrese(List<Adresa> mojeAdrese) {
        this.mojeAdrese = mojeAdrese;
    }

    public String getNovaAdresa() {
        return novaAdresa;
    }

    public void setNovaAdresa(String novaAdresa) {
        this.novaAdresa = novaAdresa;
    }

    public List<MeteoPodaci> getZadnjiMeteo() {
        return zadnjiMeteo;
    }

    public void setZadnjiMeteo(List<MeteoPodaci> zadnjiMeteo) {
        this.zadnjiMeteo = zadnjiMeteo;
    }

    public List<String> getSveAdresePopis() {
        return sveAdresePopis;
    }

    public void setSveAdresePopis(List<String> sveAdresePopis) {
        this.sveAdresePopis = sveAdresePopis;
    }

    public String getOdabranaAdresa() {
        return odabranaAdresa;
    }

    public void setOdabranaAdresa(String odabranaAdresa) {
        this.odabranaAdresa = odabranaAdresa;
    }

    private SoapJedanMeteo trenutniMeteoZaAdresu(java.lang.String adresa, java.lang.String korisnickoIme, java.lang.String lozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP port = service.getGeoMeteoSOAPPort();
        return port.trenutniMeteoZaAdresu(adresa, korisnickoIme, lozinka);
    }

    private SoapListaAdresa rangListaAdresa(java.lang.String korisnickoIme, java.lang.String lozinka, int broj) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP port = service.getGeoMeteoSOAPPort();
        return port.rangListaAdresa(korisnickoIme, lozinka, broj);
    }

    private SoapListaAdresa listaDodanihAdresa(java.lang.String korisnickoIme, java.lang.String lozinka) {
        // Note that the injected javax.xml.ws.Service reference as well as port objects are not thread safe.
        // If the calling of port operations may lead to race condition some synchronization is required.
        org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP port = service.getGeoMeteoSOAPPort();
        return port.listaDodanihAdresa(korisnickoIme, lozinka);
    }

    public KorisniciFacade getKorisniciFacade() {
        return korisniciFacade;
    }

    public void setKorisniciFacade(KorisniciFacade korisniciFacade) {
        this.korisniciFacade = korisniciFacade;
    }

    

    public MapModel getModel() {
        return model;
    }

    public void setModel(MapModel model) {
        this.model = model;
    }

    public String getCentar() {
        return centar;
    }

    public void setCentar(String centar) {
        this.centar = centar;
    }

}
