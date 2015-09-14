/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ejb.sb;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici;
import org.foi.nwtis.jprimora.ejb.eb.Korisnici_;

/**
 *
 * @author Josip
 */
@Stateless
public class KorisniciFacade extends AbstractFacade<Korisnici> {
    @PersistenceContext(unitName = "jprimora_aplikacija_2_EJBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KorisniciFacade() {
        super(Korisnici.class);
    }
    
    public int dajKorisnikId(String korisnickoIme) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
        Root<Korisnici> a = cq.from(Korisnici.class);
        Predicate uvjet = cb.equal(a.get(Korisnici_.korisnickoime), korisnickoIme);
        cq.where(uvjet);
        TypedQuery<Korisnici> q = em.createQuery(cq);
        List<Korisnici> korisnici = q.getResultList();
        if(!korisnici.isEmpty()){
            Korisnici korisnik = korisnici.get(0);
            return korisnik.getIdkorisnik();
        }
        else{
            return -1;
        } 
    }
    
    public String provjeriKorisnika(String korisnickoIme,String lozinka){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
        Root<Korisnici> a = cq.from(Korisnici.class);
        List<Predicate> predicates = new ArrayList<>();
        Predicate uvjet = cb.equal(a.get(Korisnici_.korisnickoime), korisnickoIme);
        predicates.add(uvjet);
        Predicate uvjet2 = cb.equal(a.get(Korisnici_.lozinka), lozinka);
        predicates.add(uvjet2);
        cq.select(a).where(predicates.toArray(new Predicate[]{}));
        TypedQuery<Korisnici> q = em.createQuery(cq);
        List<Korisnici> korisnici = q.getResultList();
        if(korisnici.isEmpty()){
            return "NOUSER";
        }
        else{
            return korisnici.get(0).getTipkorisnika();
        } 
    }
    
    public boolean updateKorisnik(Korisnici korisnik){
        Korisnici stariKorisnik = find(korisnik.getIdkorisnik());
        if(stariKorisnik == null){
            return false;
        }
        
        CriteriaBuilder cb = this.em.getCriteriaBuilder();

        CriteriaUpdate<Korisnici> update = cb.createCriteriaUpdate(Korisnici.class);

        Root<Korisnici> a = update.from(Korisnici.class);

        update.set(Korisnici_.lozinka,korisnik.getLozinka());
        update.set(Korisnici_.ime,korisnik.getIme());
        update.set(Korisnici_.prezime,korisnik.getPrezime());
        update.set(Korisnici_.email,korisnik.getEmail());
        update.set(Korisnici_.tipkorisnika,korisnik.getTipkorisnika());
        
        update.where(cb.equal(a.get(Korisnici_.idkorisnik),korisnik.getIdkorisnik()));

        this.em.createQuery(update).executeUpdate();
        return true;
    }
    
    public Korisnici dajKorisnik(String korisnickoIme) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> cq = cb.createQuery(Korisnici.class);
        Root<Korisnici> a = cq.from(Korisnici.class);
        Predicate uvjet = cb.equal(a.get(Korisnici_.korisnickoime), korisnickoIme);
        cq.where(uvjet);
        TypedQuery<Korisnici> q = em.createQuery(cq);
        List<Korisnici> korisnici = q.getResultList();
        if(!korisnici.isEmpty()){
            Korisnici korisnik = korisnici.get(0);
            return korisnik;
        }
        else{
            return null;
        } 
    }
    
}
