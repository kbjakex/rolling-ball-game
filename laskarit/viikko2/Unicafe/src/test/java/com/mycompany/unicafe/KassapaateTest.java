package com.mycompany.unicafe;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class KassapaateTest {

    Kassapaate paate;

    @Before
    public void setUp() {
        paate = new Kassapaate();
    }

    @Test
    public void konstruktoriAsettaaArvotOikein() {
        assertEquals(100000, paate.kassassaRahaa());
        assertEquals(0, paate.edullisiaLounaitaMyyty());
        assertEquals(0, paate.maukkaitaLounaitaMyyty());
    }
    
    @Test
    public void syoEdullisestiRiittavallaKateismaksullaKasvattaaKassanRahaa() {
        paate.syoEdullisesti(1000);
        assertEquals(100000 + 240, paate.kassassaRahaa());
    }
    
    @Test
    public void syoMaukkaastiRiittavallaKateismaksullaKasvattaaKassanRahaa() {
        paate.syoMaukkaasti(1000);
        assertEquals(100000 + 400, paate.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiRiittavallaKateismaksullaLaskeeVaihtorahanOikein() {
        assertEquals(1000-240, paate.syoEdullisesti(1000));
    }

    @Test
    public void syoMaukkaastiRiittavallaKateismaksullaLaskeeVaihtorahanOikein() {
        assertEquals(1000-400, paate.syoMaukkaasti(1000));
    }
    
    @Test
    public void syoEdullisstiRiittavallaKateismaksullaKasvattaaMyytyjenLounaidenMaaraa() {
        paate.syoEdullisesti(240);
        assertEquals(1, paate.edullisiaLounaitaMyyty());
    }
    
    @Test
    public void syoMaukkaastiRiittavallaKateismaksullaKasvattaaMyytyjenLounaidenMaaraa() {
        paate.syoMaukkaasti(400);
        assertEquals(1, paate.maukkaitaLounaitaMyyty());
    }
    
    @Test
    public void riittamatonKateismaksuEiLisaaKassaanRahaa() {
        paate.syoMaukkaasti(399);
        assertEquals(100000, paate.kassassaRahaa());
        paate.syoEdullisesti(239);
        assertEquals(100000, paate.kassassaRahaa());
    }
    
    @Test
    public void riittamatonKateismaksuPalauttaaKaikkiRahat() {
        assertEquals(399, paate.syoMaukkaasti(399));
        assertEquals(239, paate.syoEdullisesti(239));
    }
    
    @Test
    public void maukkaanLounaanOstoRiittavallaKateismaksullaKasvattaaKassanRahaa() {
        paate.syoMaukkaasti(1000);
        assertEquals(100000 + 400, paate.kassassaRahaa());
    }
    
    @Test
    public void syoEdullisestiToimiiJosKortillaTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(1000);
        assertEquals(true, paate.syoEdullisesti(kortti));
        assertEquals(1000-240, kortti.saldo());
        assertEquals(100000, paate.kassassaRahaa());
        assertEquals(1, paate.edullisiaLounaitaMyyty());
    }
    
    @Test
    public void syoMaukkaastiToimiiJosKortillaTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(1000);
        assertEquals(true, paate.syoMaukkaasti(kortti));
        assertEquals(1000-400, kortti.saldo());
        assertEquals(100000, paate.kassassaRahaa());
        assertEquals(1, paate.maukkaitaLounaitaMyyty());
    }
    
    @Test
    public void syoEdullisestiToimiiJosKortillaEiTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(239);
        assertEquals(false, paate.syoEdullisesti(kortti));
        assertEquals(239, kortti.saldo());
        assertEquals(100000, paate.kassassaRahaa());
        assertEquals(0, paate.edullisiaLounaitaMyyty());
    }
    
    @Test
    public void syoMaukkaastiToimiiJosKortillaEiTarpeeksiRahaa() {
        Maksukortti kortti = new Maksukortti(399);
        assertEquals(false, paate.syoMaukkaasti(kortti));
        assertEquals(399, kortti.saldo());
        assertEquals(100000, paate.kassassaRahaa());
        assertEquals(0, paate.maukkaitaLounaitaMyyty());
    }
    
    @Test
    public void lataaRahaaKortilleKasvattaaKortinSaldoaJaKassanRahamaaaraa() {
        Maksukortti kortti = new Maksukortti(100);
        paate.lataaRahaaKortille(kortti, 200);
        assertEquals(300, kortti.saldo());
        assertEquals(100000+200, paate.kassassaRahaa());
    }
    
    @Test
    public void lataaRahaaKortilleNegatiivisellaSummallaEiTeeMitaan() {
        Maksukortti kortti = new Maksukortti(100);
        paate.lataaRahaaKortille(kortti, -200);
        assertEquals(100, kortti.saldo());
        assertEquals(100000, paate.kassassaRahaa());
    }
}
