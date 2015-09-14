/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.jprimora.web.slusaci;

/**
 * Sucelje ServerSustava koje maskira pristup serveru da dretve ne mogu pristupiti svim public metodama unutar servera.
 * @author Josip
 */
public interface ServerSucelje {
    public boolean isPauza();
    public void setPauza(boolean pauza);
    public void stopiraj();
    public int getBrojDretvi();
    public void setStopiran(boolean stopiran);
    public boolean isStopiran();
    public void startaj();
}
