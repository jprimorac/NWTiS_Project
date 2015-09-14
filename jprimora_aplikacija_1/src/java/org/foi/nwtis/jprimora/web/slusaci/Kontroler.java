/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.foi.nwtis.jprimora.dodaci.BazaKontroler;
import org.foi.nwtis.jprimora.konfiguracije.Konfiguracija;
import org.foi.nwtis.jprimora.web.podaci.Dnevnik;

/**
 *
 * @author Josip
 */
public class Kontroler extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String korisnickoIme = request.getRemoteUser();
        HttpSession hs = request.getSession(true);
        if (hs.getAttribute("korisnickoIme") == null) {
            hs.setAttribute("korisnickoIme", korisnickoIme);
        }

        String zahtjev = request.getServletPath();
        String odrediste = null;
        BazaKontroler baza;
        switch (zahtjev) {
            case "/privatno/PregledPodataka":
                baza = new BazaKontroler(SlusacPokretac.getBP_Konfig());
                float sredstva = baza.provjeriSredstva(korisnickoIme);
                request.setAttribute("sredstva", sredstva);
                odrediste = "/privatno/pregledPodataka.jsp";
                break;
            case "/privatno/DodajSredstva":
                float dodatak = Float.parseFloat(request.getParameter("dodatak"));
                dodatak = 0 - dodatak;
                baza = new BazaKontroler(SlusacPokretac.getBP_Konfig());
                baza.oduzmiSredstva(korisnickoIme, dodatak);
                float sredstva2 = baza.provjeriSredstva(korisnickoIme);
                String ipAdresaKor = request.getRemoteAddr();
                baza.evidentirajDodavanje(korisnickoIme,sredstva2, ipAdresaKor);
                request.setAttribute("sredstva", sredstva2);
                odrediste = "/privatno/pregledPodataka.jsp";
                break;

            case "/privatno/PregledTransakcija":
                baza = new BazaKontroler(SlusacPokretac.getBP_Konfig());
                List<Dnevnik> listaDnevnika1 = baza.dajDnevnik("", korisnickoIme, "", "");
                hs.setAttribute("tablica", listaDnevnika1);
                odrediste = "/privatno/pregledTransakcija.jsp";
                Konfiguracija konfig = SlusacPokretac.getKonfig();
                String stranicenje = konfig.dajPostavku("stranicenje");
                hs.setAttribute("stranicenje", stranicenje);
                break;
            case "/privatno/PregledTransakcijaFilter":
                baza = new BazaKontroler(SlusacPokretac.getBP_Konfig());
                String ipAdresa = request.getParameter("ipAdresa");
                String vrijemeOd = request.getParameter("vrijemeOd");
                if(!vrijemeOd.equals("")){
                    vrijemeOd = vratiUnixTime(vrijemeOd);
                }
                String vrijemeDo = request.getParameter("vrijemeDo");
                if(!vrijemeDo.equals("")){
                    vrijemeDo = vratiUnixTime(vrijemeDo);
                }
                List<Dnevnik> listaDnevnika2 = baza.dajDnevnik(ipAdresa, korisnickoIme, vrijemeOd, vrijemeDo);
                hs.setAttribute("tablica", listaDnevnika2);
                odrediste = "/privatno/pregledTransakcija.jsp";
                Konfiguracija konfig2 = SlusacPokretac.getKonfig();
                String stranicenje2 = konfig2.dajPostavku("stranicenje");
                hs.setAttribute("stranicenje", stranicenje2);
                break;

        }
        if (odrediste == null) {
            throw new ServletException("Zahtjev: '" + zahtjev + "' nije poznat");
        }
        RequestDispatcher rd = this.getServletContext().getRequestDispatcher(odrediste);
        rd.forward(request, response);

    }
    
    private String vratiUnixTime(String datum){
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH.mm.ss");
        Date date = new Date();
        try {
            date = formatter.parse(datum);
        } catch (ParseException ex) {
            Logger.getLogger(Kontroler.class.getName()).log(Level.SEVERE, null, ex);
        }
        long sekunde = date.getTime() / 1000;
        return String.valueOf(sekunde);
    }
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
