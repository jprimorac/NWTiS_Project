/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.jms;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 *
 * @author Josip
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/NWTiS_jprimora_1"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class JMSCitac1 implements MessageListener {
    
    @EJB
    private CuvarPoruka cuvarPoruka;
    
    public JMSCitac1() {
    }
    
    @Override
    public void onMessage(Message message) {
        
        ObjectMessage objektPoruka = (ObjectMessage) message;
        
        JMSPoruka jms;
        try {
            jms = (JMSPoruka) objektPoruka.getObject();
            cuvarPoruka.dodajUPrve(jms);
            PorukeEndpoint.obavijestiPromjenu("Osvjezi JMS1");
            System.out.println("JMS radi" + jms.getBrojPoruka());
        } catch (JMSException ex) {
            Logger.getLogger(JMSCitac1.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
}
