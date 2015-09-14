/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.dodaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.jprimora.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.jprimora.rest.klijenti.GMKlijent;
import org.foi.nwtis.jprimora.rest.klijenti.OWMKlijent;
import org.foi.nwtis.jprimora.web.podaci.Adresa;
import org.foi.nwtis.jprimora.web.podaci.Dnevnik;
import org.foi.nwtis.jprimora.web.podaci.Lokacija;
import org.foi.nwtis.jprimora.web.podaci.MeteoPodaci;

/**
 * Klasa sluzi kao medusloj izmecu logike i pristupa bazi. Preko svojih metoda
 * preuzima i dodaje podatke u bazu.
 *
 * @author Josip
 */
public class BazaKontroler {

    private BP_Konfiguracija bp;

    public BazaKontroler(BP_Konfiguracija bp) {
        this.bp = bp;
    }

    /**
     * Metoda preuzima sve adrese spremljene u bazu.
     *
     * @return
     */
    public List<Adresa> preuzmiAdrese() {

        try {

            if (bp == null) {
                return null;
            }
            String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
            Class.forName(bp.getDriver_database(connUrl));
            String upit = "SELECT * FROM jprimora_adrese";

            try (
                    Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                    Statement stmt = conn.createStatement();) {

                ResultSet rs = stmt.executeQuery(upit);
                List<Adresa> adrese = new ArrayList<>();

                while (rs.next()) {
                    Adresa a = new Adresa(rs.getInt("ID"), rs.getString("ADRESA"), new Lokacija(rs.getString("LATITUDE"), rs.getString("LONGITUDE")));
                    adrese.add(a);
                }
                return adrese;
            } catch (SQLException ex) {
                return null;
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(BazaKontroler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Metoda preuzima zadnje podatke iz baze za adresu koja joj je
     * proslijedjena u obliku stringa. Tocnije, metoda preuzima zadnji red iz
     * tablice jprimora_meteo gdje je vrijednost id-a jednaka id-u adrese.
     *
     * @param adresa
     * @return
     */
    public MeteoPodaci preuzmiZadnjeMeteoPodatke(String adresa) {

        int idAdrese = preuziIdAdrese(adresa);
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT * FROM jprimora_meteo where idadresa=" + idAdrese + " ORDER BY id DESC FETCH FIRST 1 ROWS ONLY";
        MeteoPodaci meteoPodaci = null;
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                meteoPodaci = new MeteoPodaci(rs.getFloat("TEMPERATURA"), rs.getFloat("VLAZNOST"), rs.getFloat("TLAK"), rs.getFloat("VJETAR"), rs.getInt("OBLACI"));
                meteoPodaci.setAdresa(adresa);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BazaKontroler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return meteoPodaci;
    }

    /**
     * Metoda preuzima sve meteo podatke iz tablice jprimora_meteo za za
     * navedenu adresu.
     *
     * @param adresa
     * @return
     */
    public List<MeteoPodaci> preuzmiSveMeteoPodatke(String adresa) {
        int idAdrese = preuziIdAdrese(adresa);
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT * FROM jprimora_meteo where idadresa=" + idAdrese + "";
        List<MeteoPodaci> listaMeteoPodataka = new ArrayList<>();
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                MeteoPodaci meteoPodaci = new MeteoPodaci(rs.getFloat("TEMPERATURA"), rs.getFloat("VLAZNOST"), rs.getFloat("TLAK"), rs.getFloat("VJETAR"), rs.getInt("OBLACI"));
                meteoPodaci.setAdresa(adresa);
                listaMeteoPodataka.add(meteoPodaci);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BazaKontroler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return listaMeteoPodataka;
    }

    /**
     * Metoda izvršava upite koji dodaju (insert) podatke u tablicu. Parametar
     * koji prima su SQL upiti koji ce se izvrsiti.
     *
     * @param upit
     * @return
     */
    public boolean insertUpit(String upit) {
        if (bp == null) {
            return false;
        }
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";

        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            int uspjesno = stmt.executeUpdate(upit,Statement.RETURN_GENERATED_KEYS);
            if(uspjesno >0){
                return true;
            }else{
                return false;
            }
            

        } catch (SQLException ex) {
            return false;
        }
    }

    /**
     * Metoda za adresu koja joj je prosljedjena trazi ID u bazi podataka.
     *
     * @param adresa
     * @return
     */
    public int preuziIdAdrese(String adresa) {

        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT id FROM jprimora_adrese where adresa='" + adresa + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException ex) {
            return -1;
        }
    }

    public String provjeriKorisnika(String korisnickoIme, String lozinka) {
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT TipKorisnika FROM korisnici where KorisnickoIme='" + korisnickoIme + "' and Lozinka='" + lozinka + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                int tip = rs.getInt("TipKorisnika");
                if (tip == 1) {
                    return "ADMIN";
                } else if (tip == 2) {
                    return "USER";
                }
            }
            return "ERROR";
        } catch (SQLException ex) {
            return "ERROR";
        }
    }

    public String provjeriAdmina(String korisnickoIme) {
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT TipKorisnika FROM korisnici where KorisnickoIme='" + korisnickoIme + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                int tip = rs.getInt("TipKorisnika");
                if (tip == 1) {
                    return "ADMIN";
                } else if (tip == 2) {
                    return "USER";
                }
            }
            return "ERROR";
        } catch (SQLException ex) {
            return "ERROR";
        }
    }

    public String dodajAdmina(String korisnickoIme) {
        String jeAdmin = provjeriAdmina(korisnickoIme);
        switch (jeAdmin) {
            case "ERROR":
                return Konstante.error33;
            case "ADMIN":
                return Konstante.error34;
        }
        postaviAdmina(korisnickoIme, true);
        return "OK 10;";
    }

    public String oduzmiAdmina(String korisnickoIme) {
        String jeAdmin = provjeriAdmina(korisnickoIme);
        switch (jeAdmin) {
            case "ERROR":
                return Konstante.error33;
            case "USER":
                return Konstante.error35;
        }
        postaviAdmina(korisnickoIme, false);
        return "OK 10;";
    }

    public boolean postaviAdmina(String korisnickoIme, boolean postavi) {
        if (bp == null) {
            return false;
        }
       String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "Update korisnici set TipKorisnika=";
        if (postavi) {
            upit += "1 where KorisnickoIme='" + korisnickoIme + "'";
        } else {
            upit += "2 where KorisnickoIme='" + korisnickoIme + "'";
        }

        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            boolean uspjesno = stmt.execute(upit);
            return uspjesno;

        } catch (SQLException ex) {
            return false;
        }
    }
    
    public boolean spremiAdresu(String novaAdresa, int korisnik){
        GMKlijent gmk = new GMKlijent();
        Lokacija l = gmk.getGeoLocation(novaAdresa);
        String naredba = "Insert into jprimora_adrese(adresa,korisnik,latitude,longitude) values('" + novaAdresa +"', " + korisnik + ", '" + l.getLatitude() + "', '" + l.getLongitude() +"')";
        return insertUpit(naredba);    
    }

    public float provjeriSredstva(String korisnickoIme) {
        
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "Select sredstva from korisnici where KorisnickoIme='" + korisnickoIme + "'";

        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                return rs.getFloat("sredstva");
            }

        } catch (SQLException ex) {
            return -1;

        }
        return -1;
    }

    public boolean oduzmiSredstva(String korisnickoIme, float kolicina) {
        if (bp == null) {
            return false;
        }

        float trenutnoStanje = provjeriSredstva(korisnickoIme);
        float novoStanje = trenutnoStanje - kolicina;
        
        

        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "Update korisnici set sredstva=" + novoStanje + " where KorisnickoIme='" + korisnickoIme + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            boolean uspjesno = stmt.execute(upit);
            return uspjesno;

        } catch (SQLException ex) {
            return false;
        }
    }
    
    public int preuzmiIdKorisnika(String korisnickoIme){
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT Id FROM korisnici where KorisnickoIme='" + korisnickoIme + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException ex) {
            return -1;
        }
    }
    
    public String preuzmiTrenutneMeteoPodatkeString(String adresa, String apiKey){
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT * FROM jprimora_adrese where adresa='" + adresa + "'";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            while (rs.next()) {      
                OWMKlijent klijent = new OWMKlijent(apiKey);        
                MeteoPodaci meteo = klijent.getRealTimeWeather(rs.getString("latitude"), rs.getString("longitude"));
                String odgovor = "TEMP "+ meteo.getTemperatureValue() + " VLAGA " + meteo.getHumidityValue() + " TLAK " + meteo.getPressureValue() + " GEOSIR ";
                odgovor += rs.getString("latitude") + " GEODUZ " + rs.getString("longitude")+ ";";
                return odgovor;
            }
            return "";
        } catch (SQLException ex) {
            return "";
        }
    }
    
    public List<Adresa> dajDodaneAdreseKorisnika(String korisnickoIme){
        int idKorisnika = preuzmiIdKorisnika(korisnickoIme);
        
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT * FROM jprimora_adrese where korisnik=" + idKorisnika  ;
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            List<Adresa> listaAdresa = new ArrayList<>();
            while (rs.next()) {
                Lokacija l = new Lokacija(rs.getString("latitude"),rs.getString("longitude"));
                Adresa adresa = new Adresa(rs.getInt("id"),rs.getString("adresa"),l);
                listaAdresa.add(adresa);      
            }
            return listaAdresa;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public List<Adresa> dajRangListuAdresa(int broj){
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT jprimora_adrese.*, count(*) as 'Cnt' FROM jprimora_adrese,jprimora_meteo where jprimora_adrese.id=jprimora_meteo.IdAdresa GROUP BY jprimora_meteo.IdAdresa ORDER BY 2 DESC LIMIT " + broj;
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            List<Adresa> listaAdresa = new ArrayList<>();
            while (rs.next()) {      
                Lokacija l = new Lokacija(rs.getString("latitude"),rs.getString("longitude"));
                Adresa adresa = new Adresa(rs.getInt("id"),rs.getString("adresa"),l);
                listaAdresa.add(adresa);        
            }
            return listaAdresa;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public List<MeteoPodaci> dajPosljednjihNMeteo(String adresa, int broj){
        int idAdrese = preuziIdAdrese(adresa);
        
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT jprimora_meteo.* FROM `jprimora_meteo` WHERE IdAdresa="+idAdrese + " ORDER BY Id DESC LIMIT " + broj;
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            List<MeteoPodaci> listaMeteo = new ArrayList<>();
            while (rs.next()) {      
                MeteoPodaci meteo = new MeteoPodaci(rs.getFloat("Temperatura"), rs.getFloat("Vlaznost"), rs.getFloat("Vjetar"), rs.getFloat("Tlak"), rs.getInt("Oblaci"));
                meteo.setAdresa(adresa);
                listaMeteo.add(meteo);
            }
            return listaMeteo;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public List<MeteoPodaci> dajPrognozuInterval(String adresa, String odtad, String dotad){
        
        int idAdrese = preuziIdAdrese(adresa);
        
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT jprimora_meteo.* FROM `jprimora_meteo` WHERE IdAdresa="+idAdrese + " AND UNIX_TIMESTAMP(Vrijeme) >=" + odtad + " AND UNIX_TIMESTAMP(Vrijeme) <=" + dotad;
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            List<MeteoPodaci> listaMeteo = new ArrayList<>();
            while (rs.next()) {      
                MeteoPodaci meteo = new MeteoPodaci(rs.getFloat("Temperatura"), rs.getFloat("Vlaznost"), rs.getFloat("Vjetar"), rs.getFloat("Tlak"), rs.getInt("Oblaci"));
                meteo.setAdresa(adresa);
                listaMeteo.add(meteo);
            }
            return listaMeteo;
        } catch (SQLException ex) {
            return null;
        }
    }
    
    public int prebrojiKorisnike(){
        
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT COUNT(*) FROM korisnici";
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            
            while (rs.next()) {      
                int broj = rs.getInt("COUNT(*)");
                return broj;
            }
            return -1;
        } catch (SQLException ex) {
            return -1;
        }
    }
    
    public List<Dnevnik> dajDnevnik(String ipAdresa, String korisnickoIme, String vrijemeOd, String vrijemeDo){
        String connUrl = bp.getServer_database() + bp.getUser_database() + "?useUnicode=true&characterEncoding=UTF-8";
        String upit = "SELECT * FROM dnevnik WHERE Korisnik='" + korisnickoIme+"'";
        if(!ipAdresa.equals("")){
            upit +=" AND IpAdresa='"+ ipAdresa +"'";
        }
        if(!vrijemeOd.equals("")){
            upit +=" AND UNIX_TIMESTAMP(Vrijeme)>="+ vrijemeOd ;
        }
        if(!vrijemeDo.equals("")){
            upit +=" AND UNIX_TIMESTAMP(Vrijeme)<="+ vrijemeDo ;
        }
        List<Dnevnik> listaDnevnika = new ArrayList<>();
        try (
                Connection conn = DriverManager.getConnection(connUrl, bp.getUser_username(), bp.getUser_password());
                Statement stmt = conn.createStatement();) {

            ResultSet rs = stmt.executeQuery(upit);
            
            while (rs.next()) {      
                Dnevnik dnevnik = new Dnevnik();
                dnevnik.setIpAdresa(rs.getString("IpAdresa"));
                dnevnik.setKorisnik(rs.getString("Korisnik"));
                dnevnik.setKorisnikId(rs.getInt("KorisnikId"));
                dnevnik.setOdgovor(rs.getString("Odgovor"));
                dnevnik.setZahtjev(rs.getString("Zahtjev"));
                dnevnik.setTrajanje(rs.getInt("Trajanje"));
                dnevnik.setVrijeme(rs.getString("Vrijeme"));
                dnevnik.setSredstva(rs.getFloat("Sredstva"));
                listaDnevnika.add(dnevnik);
            }
            return listaDnevnika;
        } catch (SQLException ex) {
            return listaDnevnika;
        }
    }
    
    public boolean evidentirajDodavanje(String korisnickoIme, float sredstva, String ipAdresa){
        
        String upit = "Insert into dnevnik(IpAdresa,Korisnik,Zahtjev,Sredstva) values('" + ipAdresa + "','" + korisnickoIme + "','Dodavanje sredstava'," + sredstva + ")"; 
        return insertUpit(upit);
    }
}
