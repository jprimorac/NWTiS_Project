/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ejb.eb;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
    @NamedQuery(name = "Korisnici.findAll", query = "SELECT k FROM Korisnici k"),
    @NamedQuery(name = "Korisnici.findByIdkorisnik", query = "SELECT k FROM Korisnici k WHERE k.idkorisnik = :idkorisnik"),
    @NamedQuery(name = "Korisnici.findByKorisnickoime", query = "SELECT k FROM Korisnici k WHERE k.korisnickoime = :korisnickoime"),
    @NamedQuery(name = "Korisnici.findByLozinka", query = "SELECT k FROM Korisnici k WHERE k.lozinka = :lozinka"),
    @NamedQuery(name = "Korisnici.findByIme", query = "SELECT k FROM Korisnici k WHERE k.ime = :ime"),
    @NamedQuery(name = "Korisnici.findByPrezime", query = "SELECT k FROM Korisnici k WHERE k.prezime = :prezime"),
    @NamedQuery(name = "Korisnici.findByEmail", query = "SELECT k FROM Korisnici k WHERE k.email = :email"),
    @NamedQuery(name = "Korisnici.findByTipkorisnika", query = "SELECT k FROM Korisnici k WHERE k.tipkorisnika = :tipkorisnika")})
public class Korisnici implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    private Integer idkorisnik;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String korisnickoime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String lozinka;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String ime;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String prezime;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    private String tipkorisnika;

    public Korisnici() {
    }

    public Korisnici(Integer idkorisnik) {
        this.idkorisnik = idkorisnik;
    }

    public Korisnici(Integer idkorisnik, String korisnickoime, String lozinka, String ime, String prezime, String email, String tipkorisnika) {
        this.idkorisnik = idkorisnik;
        this.korisnickoime = korisnickoime;
        this.lozinka = lozinka;
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.tipkorisnika = tipkorisnika;
    }

    public Integer getIdkorisnik() {
        return idkorisnik;
    }

    public void setIdkorisnik(Integer idkorisnik) {
        this.idkorisnik = idkorisnik;
    }

    public String getKorisnickoime() {
        return korisnickoime;
    }

    public void setKorisnickoime(String korisnickoime) {
        this.korisnickoime = korisnickoime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public void setLozinka(String lozinka) {
        this.lozinka = lozinka;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTipkorisnika() {
        return tipkorisnika;
    }

    public void setTipkorisnika(String tipkorisnika) {
        this.tipkorisnika = tipkorisnika;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idkorisnik != null ? idkorisnik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Korisnici)) {
            return false;
        }
        Korisnici other = (Korisnici) object;
        if ((this.idkorisnik == null && other.idkorisnik != null) || (this.idkorisnik != null && !this.idkorisnik.equals(other.idkorisnik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.jprimora.ejb.eb.Korisnici[ idkorisnik=" + idkorisnik + " ]";
    }
    
}
