/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;
import org.foi.nwtis.jprimora.klijenti.Adresa;
import org.foi.nwtis.jprimora.klijenti.SoapListaAdresa;
import java.lang.reflect.Field;
import org.codehaus.jettison.json.JSONException;

/**
 * REST Web Service
 *
 * @author Josip
 */
public class MeteoRESTResource {

    KorisniciFacade korisniciFacade = lookupKorisniciFacadeBean();

    private String id;

    /**
     * Creates a new instance of MeteoRESTResource
     */
    private MeteoRESTResource(String id) {
        this.id = id;
    }

    /**
     * Get instance of the MeteoRESTResource
     */
    public static MeteoRESTResource getInstance(String id) {
        // The user may use some kind of persistence mechanism
        // to store and restore instances of MeteoRESTResource class.
        return new MeteoRESTResource(id);
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.jprimora.rest.MeteoRESTResource
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {

        JSONObject rezultat = new JSONObject();

        List<Korisnici> korisnici = korisniciFacade.findAll();

       

        try {
            for (Korisnici k : korisnici) {
                if (k.getIdkorisnik() == Long.parseLong(id)) {

                    SoapListaAdresa soapOdgovor = listaDodanihAdresaBesplatno(k.getKorisnickoime(), k.getLozinka());

                    List<Adresa> adrese = soapOdgovor.getAdrese();
                    
                    JSONArray jsonPolje = new JSONArray();
                    int brojac = 0;
                    for (Adresa a : adrese) {
                        JSONObject jsonAdresa = new JSONObject();
                        jsonAdresa.put("idadresa", a.getIdadresa());
                        jsonAdresa.put("adresa", a.getAdresa());
                        jsonAdresa.put("lat", a.getGeoloc().getLatitude());
                        jsonAdresa.put("long", a.getGeoloc().getLongitude());
                        jsonPolje.put(brojac,jsonAdresa);
                        brojac++;
                    }
                    rezultat.put("adrese", jsonPolje);
                } 
            }
            return rezultat.toString();
        } catch (JSONException ex) {
            Logger.getLogger(MeteoRESTResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * PUT method for updating or creating an instance of MeteoRESTResource
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/json")
    public void putJson(String content) {
    }

    /**
     * DELETE method for resource MeteoRESTResource
     */
    @DELETE
    public void delete() {
    }

    private KorisniciFacade lookupKorisniciFacadeBean() {
        try {
            Context c = new InitialContext();
            return (KorisniciFacade) c.lookup("java:global/jprimora_aplikacija_2/jprimora_aplikacija_2_EJB/KorisniciFacade!org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

    private static SoapListaAdresa listaDodanihAdresaBesplatno(java.lang.String korisnickoIme, java.lang.String lozinka) {
        org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP_Service service = new org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP_Service();
        org.foi.nwtis.jprimora.klijenti.GeoMeteoSOAP port = service.getGeoMeteoSOAPPort();
        return port.listaDodanihAdresaBesplatno(korisnickoIme, lozinka);
    }
}
