/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.rest;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade;

/**
 * REST Web Service
 *
 * @author Josip
 */
@Path("/ms")
public class MeteoRESTsResourceContainer {

    KorisniciFacade korisniciFacade = lookupKorisniciFacadeBean();

    @Context
    private UriInfo context;
    @Context
    private ServletContext sce;

    /**
     * Creates a new instance of MeteoRESTsResourceContainer
     */
    public MeteoRESTsResourceContainer() {
    }

    /**
     * Retrieves representation of an instance of
     * org.foi.nwtis.jprimora.rest.MeteoRESTsResourceContainer
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/json")
    public String getJson() {

        //ServletContext servletContext = (ServletContext) FacesContext
        //.getCurrentInstance().getExternalContext().getContext();
        List<Korisnici> aktivniKorisnici = (List<Korisnici>) sce.getAttribute("aktivniKorisnici");
        JSONObject rezultat = new JSONObject();
        JSONArray jo1 = new JSONArray();
        int i = 0;
        for (Korisnici k : aktivniKorisnici) {
            try {
                JSONObject jo2 = new JSONObject();
                jo2.put("idkorisnik", Integer.toString(k.getIdkorisnik()));
                jo2.put("korisnickoIme", k.getKorisnickoime());
                jo2.put("lozinka", k.getLozinka());
                jo2.put("ime", k.getIme());
                jo2.put("prezime", k.getPrezime());
                jo2.put("email", k.getEmail());
                jo2.put("tipKorisnika", k.getTipkorisnika());
                jo1.put(i, jo2);
            } catch (JSONException ex) {
                Logger.getLogger(MeteoRESTsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
            }
            i++;
        }
        try {
            rezultat.put("korisnici", jo1);
        } catch (JSONException ex) {
            Logger.getLogger(MeteoRESTsResourceContainer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rezultat.toString();
    }

    /**
     * POST method for creating an instance of MeteoRESTResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response postJson(String content) {
        //TODO
        return Response.created(context.getAbsolutePath()).build();
    }

    /**
     * Sub-resource locator method for {id}
     */
    @Path("{id}")
    public MeteoRESTResource getMeteoRESTResource(@PathParam("id") String id) {
        return MeteoRESTResource.getInstance(id);
    }

    private KorisniciFacade lookupKorisniciFacadeBean() {
        try {
            javax.naming.Context c = new InitialContext();
            return (KorisniciFacade) c.lookup("java:global/jprimora_aplikacija_2/jprimora_aplikacija_2_EJB/KorisniciFacade!org.foi.nwtis.jprimora.ejb.sb.KorisniciFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
