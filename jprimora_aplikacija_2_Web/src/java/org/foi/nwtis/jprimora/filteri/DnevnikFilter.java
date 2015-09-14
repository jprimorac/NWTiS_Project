/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.filteri;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.jprimora.ejb.sb.DnevnikFacade;
import org.foi.nwtis.jprimora.ejb.eb.Dnevnik;

/**
 *
 * @author Josip
 */
@WebFilter(filterName = "DnevnikFilter", urlPatterns = {"/faces/korisnik/*", "/korisnik/*", "/faces/prijava.xhtml", "/prijava.xhtml", "/faces/odjava.xhtml", "/odjava.xhtml"})
public class DnevnikFilter implements Filter {

    DnevnikFacade dnevnikFacade = lookupDnevnikFacadeBean();

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;

    public DnevnikFilter() {
    }

    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        long start = System.currentTimeMillis();
        String ipAdresa = request.getRemoteAddr();
        String URL = ((HttpServletRequest) request).getRequestURI();
        Dnevnik zapis = new Dnevnik();
        zapis.setIpadresa(ipAdresa);
        zapis.setUrl(URL);
        java.sql.Timestamp sqlDate = new java.sql.Timestamp(new java.util.Date().getTime());
        zapis.setVrijeme(sqlDate);


        chain.doFilter(request, response);

        zapis.setTrajanje((int) (System.currentTimeMillis() - start));

        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession sesija = req.getSession(false);

        if (sesija != null) {
            String korisnickoIme = (String) sesija.getAttribute("korisnik");
            if (korisnickoIme != null) {
                zapis.setKorisnik(korisnickoIme);
            }else{
                zapis.setKorisnik("Nepoznat");
            }
        }else{
            zapis.setKorisnik("Nepoznat");
        }

        zapis.setZahtjev("?");
        zapis.setOdgovor("?");
        dnevnikFacade.create(zapis);

    }

    /**
     * Return the filter configuration object for this filter.
     */
    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    /**
     * Set the filter configuration object for this filter.
     *
     * @param filterConfig The filter configuration object
     */
    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    /**
     * Destroy method for this filter
     */
    public void destroy() {
    }

    /**
     * Init method for this filter
     */
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("DnevnikFilter()");
        }
        StringBuffer sb = new StringBuffer("DnevnikFilter(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    private DnevnikFacade lookupDnevnikFacadeBean() {
        try {
            Context c = new InitialContext();
            return (DnevnikFacade) c.lookup("java:global/jprimora_aplikacija_2/jprimora_aplikacija_2_EJB/DnevnikFacade!org.foi.nwtis.jprimora.ejb.sb.DnevnikFacade");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }

}
