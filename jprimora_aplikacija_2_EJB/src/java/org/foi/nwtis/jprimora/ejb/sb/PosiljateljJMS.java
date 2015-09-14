/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.ejb.sb;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

/**
 *
 * @author Josip
 */
@Singleton
@LocalBean
public class PosiljateljJMS {
    @Resource(mappedName = "jms/NWTiS_jprimora_2")
    private Queue nWTiS_jprimora_2;
    @Inject
    @JMSConnectionFactory("jms/NWTiS_QF_jprimora_1")
    private JMSContext context;
    
    @Resource(mappedName = "jms/NWTiS_jprimora_1")
    private Queue nWTiS_jprimora_1;
    @Inject
    @JMSConnectionFactory("jms/NWTiS_QF_jprimora_2")
    private JMSContext context2;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

    public void sendJMSMessageToNWTiS_jprimora_1(Serializable messageData) {
        context.createProducer().send(nWTiS_jprimora_1, messageData);
   }

    public void sendJMSMessageToNWTiS_jprimora_2(Serializable messageData) {
        context2.createProducer().send(nWTiS_jprimora_2, messageData);
    }
    
    
}
