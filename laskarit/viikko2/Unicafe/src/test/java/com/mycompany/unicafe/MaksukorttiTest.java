package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class MaksukorttiTest {

    Maksukortti kortti;

    @Before
    public void setUp() {
        kortti = new Maksukortti(10);
    }

    @Test
    public void luotuKorttiOlemassa() {
        assertTrue(kortti!=null);      
    }
    
    @Test
    public void konstruktoriAsettaaSaldonOikein() {
        assertEquals(10, kortti.saldo());
        assertEquals("saldo: 0.10", kortti.toString());
    }
    
    @Test
    public void rahanLataaminenKasvattaaSaldoa() {
        kortti.lataaRahaa(120);
        assertEquals(130, kortti.saldo());
    }
    
    @Test
    public void saldoVaheneeRahaaOttaessaJosRahaaTarpeeksi() {
        kortti.otaRahaa(9);
        assertEquals(1, kortti.saldo());
        kortti.otaRahaa(1);
        assertEquals(0, kortti.saldo());
    }
    
    @Test
    public void saldoEiVaheneRahaaOttaessaJosRahaEiTarpeeksi() {
        kortti.otaRahaa(11);
        assertEquals(10, kortti.saldo());
    }
    
    @Test
    public void otaRahaaPalauttaaTrueJosRahaRiittaa() {
        assertEquals(true, kortti.otaRahaa(5));
        assertEquals(true, kortti.otaRahaa(5));
        assertEquals(false, kortti.otaRahaa(5));
    }
}
