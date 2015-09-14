/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.jprimora.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;

/**
 * Web application lifecycle listener.
 *
 * @author Josip
 */
public class SlusacPokretac implements ServletContextListener {
    
    
    private static Konfiguracija konfig;
    private static BP_Konfiguracija bpk;
    private PrimitivniServer server;


    @Override
    public void contextInitialized(ServletContextEvent sce) {

        String direktorij = sce.getServletContext().getRealPath("/WEB-INF")
                + java.io.File.separator;
        String datoteka = direktorij + 
                sce.getServletContext().getInitParameter("BP_konfig");
        bpk = new BP_Konfiguracija(datoteka);
        datoteka = direktorij + 
                sce.getServletContext().getInitParameter("konfig");
        
        
        
        try {
            konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(datoteka);
            //sce.getServletContext().setAttribute("konfig", konfig);
            
        } catch (NemaKonfiguracije ex) {
            Logger.getLogger(SlusacPokretac.class.getName()).log(Level.SEVERE, null, ex);
        }
        konfig.spremiPostavku("direktorij", direktorij);
        server = new PrimitivniServer(konfig, bpk);
        server.start();
 
    }
    
    public static BP_Konfiguracija getBP_Konfig(){
        return bpk;
    }
    
    public static Konfiguracija getKonfig(){
        return konfig;
    }
    
  

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       server.stopiraj();
    }

    
    
}
