/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimoora.ws;

import java.util.Date;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.jprimora.web.podaci.SOAPJedanMeteo;
import org.foi.nwtis.jprimora.web.podaci.SOAPListaMeteo;
import org.foi.nwtis.jprimora.web.podaci.SOAPListaAdresa;

/**
 *
 * @author Josip
 */
@WebService(serviceName = "GeoMeteoSOAP")
public class GeoMeteoSOAP {

    /**
     * Web service operation
     *
     * @param adresa
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "trenutniMeteoZaAdresu")
    public SOAPJedanMeteo trenutniMeteoZaAdresu(@WebParam(name = "adresa") String adresa, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPJedanMeteo SOAPmeteo = new SOAPJedanMeteo();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("trenutni", korisnickoIme)) {
                SOAPmeteo.setMeteo(helper.dajTrenutneMeteoPodatke(adresa));
                helper.oduzmiSredstva("trenutni", korisnickoIme);
                SOAPmeteo.setOdgovor("OK 200");
            } else {
                SOAPmeteo.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPmeteo.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPmeteo;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "listaDodanihAdresa")
    public SOAPListaAdresa listaDodanihAdresa(@WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaAdresa SOAPodgovor = new SOAPListaAdresa();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("dodaneAdrese", korisnickoIme)) {
                SOAPodgovor.setAdrese(helper.dajDodaneAdreseKorisnika(korisnickoIme));
                helper.oduzmiSredstva("dodaneAdrese", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "rangListaAdresa")
    public SOAPListaAdresa rangListaAdresa(@WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka, @WebParam(name = "broj") int broj) {
        SOAPListaAdresa SOAPodgovor = new SOAPListaAdresa();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("rangLista", korisnickoIme)) {
                SOAPodgovor.setAdrese(helper.dajRangListuAdresa(broj));
                helper.oduzmiSredstva("rangLista", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "posljednjiNMeteoZaAdresu")
    public SOAPListaMeteo posljednjiNMeteoZaAdresu(@WebParam(name = "adresa") String adresa, @WebParam(name = "broj") int broj, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaMeteo SOAPodgovor = new SOAPListaMeteo();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("posljednjiN", korisnickoIme)) {
                SOAPodgovor.setListaMeteo(helper.dajPosljednjihNMeteo(adresa, broj));
                helper.oduzmiSredstva("posljednjiN", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "meteoZaAdresuUIntervalu")
    public SOAPListaMeteo meteoZaAdresuUIntervalu(@WebParam(name = "adresa") String adresa, @WebParam(name = "odtad") String odtad, @WebParam(name = "dotad") String dotad, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaMeteo SOAPodgovor = new SOAPListaMeteo();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("intervalPrognoza", korisnickoIme)) {
                SOAPodgovor.setListaMeteo(helper.dajPrognozuInterval(adresa, odtad, dotad));
                helper.oduzmiSredstva("intervalPrognoza", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "staniceNUBliziniAdrese")
    public SOAPListaAdresa staniceNUBliziniAdrese(@WebParam(name = "adresa") String adresa, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka, @WebParam(name = "broj") String broj) {
        SOAPListaAdresa SOAPodgovor = new SOAPListaAdresa();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("stanice", korisnickoIme)) {
                SOAPodgovor.setAdrese(helper.dajStaniceBlizu(adresa, broj));
                helper.oduzmiSredstva("stanice", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "prognozaZaNSatiZaAdresu")
    public SOAPListaMeteo prognozaZaNSatiZaAdresu(@WebParam(name = "adresa") String adresa, @WebParam(name = "broj") int broj, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaMeteo SOAPodgovor = new SOAPListaMeteo();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("prognozaSati", korisnickoIme)) {
                SOAPodgovor.setListaMeteo(helper.dajPrognozuSati(adresa, broj));
                helper.oduzmiSredstva("prognozaSati", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "prognozaZaNDanaZaAdresu")
    public SOAPListaMeteo prognozaZaNDanaZaAdresu(@WebParam(name = "adresa") String adresa, @WebParam(name = "broj") int broj, @WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaMeteo SOAPodgovor = new SOAPListaMeteo();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            if (helper.provjeriSredstva("prognozaDani", korisnickoIme)) {
                SOAPodgovor.setListaMeteo(helper.dajPrognozuDani(adresa, broj));
                helper.oduzmiSredstva("prognozaDani", korisnickoIme);
                SOAPodgovor.setOdgovor("OK 200");
            } else {
                SOAPodgovor.setOdgovor("ERROR: Korisnik nema dovoljno sredstva za traženu akciju.");
            }
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }

        return SOAPodgovor;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "listaDodanihAdresaBesplatno")
    public SOAPListaAdresa listaDodanihAdresaBesplatno(@WebParam(name = "korisnickoIme") String korisnickoIme, @WebParam(name = "lozinka") String lozinka) {
        SOAPListaAdresa SOAPodgovor = new SOAPListaAdresa();
        GeoMeteoHelper helper = new GeoMeteoHelper();
        if (helper.autenticirajKorisnika(korisnickoIme, lozinka)) {
            SOAPodgovor.setAdrese(helper.dajDodaneAdreseKorisnika(korisnickoIme));
            SOAPodgovor.setOdgovor("OK 200");
        } else {
            SOAPodgovor.setOdgovor("ERROR: Korisnik ne postoji ili lozinka nije ispravna.");
        }
        return SOAPodgovor;
    }
}
