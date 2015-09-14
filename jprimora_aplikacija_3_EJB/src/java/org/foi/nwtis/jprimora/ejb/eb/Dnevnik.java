/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Josip
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Dnevnik.findAll", query = "SELECT d FROM Dnevnik d"),
    @NamedQuery(name = "Dnevnik.findByIddnevnik", query = "SELECT d FROM Dnevnik d WHERE d.iddnevnik = :iddnevnik"),
    @NamedQuery(name = "Dnevnik.findByKorisnik", query = "SELECT d FROM Dnevnik d WHERE d.korisnik = :korisnik"),
    @NamedQuery(name = "Dnevnik.findByKorisnikid", query = "SELECT d FROM Dnevnik d WHERE d.korisnikid = :korisnikid"),
    @NamedQuery(name = "Dnevnik.findByIpadresa", query = "SELECT d FROM Dnevnik d WHERE d.ipadresa = :ipadresa"),
    @NamedQuery(name = "Dnevnik.findByZahtjev", query = "SELECT d FROM Dnevnik d WHERE d.zahtjev = :zahtjev"),
    @NamedQuery(name = "Dnevnik.findByOdgovor", query = "SELECT d FROM Dnevnik d WHERE d.odgovor = :odgovor"),
    @NamedQuery(name = "Dnevnik.findBySredtva", query = "SELECT d FROM Dnevnik d WHERE d.sredtva = :sredtva"),
    @NamedQuery(name = "Dnevnik.findByUrl", query = "SELECT d FROM Dnevnik d WHERE d.url = :url"),
    @NamedQuery(name = "Dnevnik.findByVrijeme", query = "SELECT d FROM Dnevnik d WHERE d.vrijeme = :vrijeme"),
    @NamedQuery(name = "Dnevnik.findByTrajanje", query = "SELECT d FROM Dnevnik d WHERE d.trajanje = :trajanje"),
    @NamedQuery(name = "Dnevnik.findByStatus", query = "SELECT d FROM Dnevnik d WHERE d.status = :status")})
public class Dnevnik implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer iddnevnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String korisnik;
    private Integer korisnikid;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String ipadresa;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String zahtjev;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String odgovor;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    private Double sredtva;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String url;
    @Basic(optional = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    private Integer trajanje;
    private Integer status;

    public Dnevnik() {
    }

    public Dnevnik(Integer iddnevnik) {
        this.iddnevnik = iddnevnik;
    }

    public Dnevnik(Integer iddnevnik, String korisnik, String ipadresa, String zahtjev, String odgovor, String url, Date vrijeme) {
        this.iddnevnik = iddnevnik;
        this.korisnik = korisnik;
        this.ipadresa = ipadresa;
        this.zahtjev = zahtjev;
        this.odgovor = odgovor;
        this.url = url;
        this.vrijeme = vrijeme;
    }

    public Integer getIddnevnik() {
        return iddnevnik;
    }

    public void setIddnevnik(Integer iddnevnik) {
        this.iddnevnik = iddnevnik;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public Integer getKorisnikid() {
        return korisnikid;
    }

    public void setKorisnikid(Integer korisnikid) {
        this.korisnikid = korisnikid;
    }

    public String getIpadresa() {
        return ipadresa;
    }

    public void setIpadresa(String ipadresa) {
        this.ipadresa = ipadresa;
    }

    public String getZahtjev() {
        return zahtjev;
    }

    public void setZahtjev(String zahtjev) {
        this.zahtjev = zahtjev;
    }

    public String getOdgovor() {
        return odgovor;
    }

    public void setOdgovor(String odgovor) {
        this.odgovor = odgovor;
    }

    public Double getSredtva() {
        return sredtva;
    }

    public void setSredtva(Double sredtva) {
        this.sredtva = sredtva;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public Integer getTrajanje() {
        return trajanje;
    }

    public void setTrajanje(Integer trajanje) {
        this.trajanje = trajanje;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iddnevnik != null ? iddnevnik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Dnevnik)) {
            return false;
        }
        Dnevnik other = (Dnevnik) object;
        if ((this.iddnevnik == null && other.iddnevnik != null) || (this.iddnevnik != null && !this.iddnevnik.equals(other.iddnevnik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.jprimora.ejb.eb.Dnevnik[ iddnevnik=" + iddnevnik + " ]";
    }
    
}
