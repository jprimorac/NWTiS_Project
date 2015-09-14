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
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Josip
 */
@WebFilter(filterName = "KorisnikFilter", urlPatterns = {"/korisnik/*","/faces/korisnik/*"})
public class KorisnikFilter implements Filter {
    
    private static final boolean debug = true;

    // The filter configuration object we are associated with.  If
    // this value is null, this filter instance is not currently
    // configured. 
    private FilterConfig filterConfig = null;
    
    public KorisnikFilter() {
    }    
  
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        
        if (debug) {
            log("KorisnikFilter:doFilter()");
        }
        
        boolean nastavi = true;

        HttpSession sesija = ((HttpServletRequest) request).getSession(false);
        if (sesija == null) {
            nastavi = false;
        } else {
            if (sesija.getAttribute("korisnik") == null) {
                nastavi = false;
            } 
        }

        if (!nastavi) {
            RequestDispatcher rd = filterConfig.getServletContext().getRequestDispatcher("/faces/prijava.xhtml");
            rd.forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
             
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
            if (debug) {                
                log("KorisnikFilter:Initializing filter");
            }
        }
    }
 
    public void log(String msg) {
        filterConfig.getServletContext().log(msg);        
    }
    
}
