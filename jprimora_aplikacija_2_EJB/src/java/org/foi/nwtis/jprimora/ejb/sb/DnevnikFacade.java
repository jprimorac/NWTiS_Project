/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ejb.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.foi.nwtis.jprimora.ejb.eb.Dnevnik;
import org.foi.nwtis.jprimora.ejb.eb.Dnevnik_;

/**
 *
 * @author Josip
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> {
    @PersistenceContext(unitName = "jprimora_aplikacija_2_EJBPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }
    
    public List<Dnevnik> filterDnevnik(Date datumOd, Date datumDo, String iPadresa, int trajanjeOd, int trajanjeDo, String korisnickoIme) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dnevnik> cq = cb.createQuery(Dnevnik.class);
        Root<Dnevnik> d = cq.from(Dnevnik.class);

        List<Predicate> predicates = new ArrayList<>();
        
        if (datumOd != null) {
            Predicate uvjet1 = cb.greaterThanOrEqualTo(d.get(Dnevnik_.vrijeme), datumOd);
            predicates.add(uvjet1);
        }
        if(datumDo != null){
            Predicate uvjet2 = cb.lessThanOrEqualTo(d.get(Dnevnik_.vrijeme), datumOd);
            predicates.add(uvjet2);
        }
        if(iPadresa != null && !iPadresa.equals("")){
            Predicate uvjet3 = cb.equal(d.get(Dnevnik_.ipadresa), iPadresa);
            predicates.add(uvjet3);
        }
        if(trajanjeOd!=0){
            Predicate uvjet4 = cb.greaterThanOrEqualTo(d.get(Dnevnik_.trajanje), trajanjeOd);
            predicates.add(uvjet4);
        }
        if(trajanjeDo!=0){
            Predicate uvjet5 = cb.greaterThanOrEqualTo(d.get(Dnevnik_.trajanje), trajanjeDo);
            predicates.add(uvjet5);
        }
        if(korisnickoIme != null && !korisnickoIme.equals("")){
            Predicate uvjet6 = cb.equal(d.get(Dnevnik_.korisnik), korisnickoIme);
            predicates.add(uvjet6);
        }
        
        if(!predicates.isEmpty()){
            cq.select(d).where(predicates.toArray(new Predicate[]{}));
        }
        else{
            cq.select(d);
        }
        
        TypedQuery<Dnevnik> q = em.createQuery(cq);
        List<Dnevnik> podaci = q.getResultList();
        return podaci;
    }
    
}
