package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Robotnik extends Agent{
    private int diamenty;
    private StrategiaDnia uczenie;
    private StrategiaProdukcji produkcja;
    private StrategiaKariery zmiana;
    //private Produktywnosc produktywnosc;
    private HashMap<Ptyp, Integer> produktywnosc;
    private StrategiaKupowania kupowanie;
    private Ztyp aktualnaKariera;
    private HashMap<Ztyp, Integer> poziomyKarier;
    private int poziom;
    private boolean zyje;


    private int dniBezJedzenia;
    public Robotnik(int id, int poziom, StrategiaDnia uczenie, StrategiaProdukcji produkcja, StrategiaKariery zmiana,
                    StrategiaKupowania kupowanie, Ztyp kariera, HashMap<Pair<Ptyp, Integer>, Double> produkty,
                    HashMap<Ptyp, Integer> produktywnosc)
    {
        super(id, produkty);
        this.poziom = poziom;
        this.uczenie = uczenie;
        this.produkcja = produkcja;
        this.aktualnaKariera = kariera;
        this.kupowanie = kupowanie;
        this.zmiana = zmiana;
        dniBezJedzenia = 0;
        zyje = true;
        poziomyKarier = new HashMap<>();
        poziomyKarier.put(aktualnaKariera, poziom);
        this.produktywnosc = produktywnosc;
    }
    public Pair<ArrayList<OfertaKupnaRobotnika>, ArrayList<OfertaSprzedazyRobotnika>> przeprowadzDzien(int dzien,
                               HashMap<Pair<Ptyp, Integer>, Integer> iloscPrzedmiotow,
                                 HashMap<Pair<Ptyp, Integer>, Double> SrednieCeny, int kara_za_brak_ubran)
    {
        ArrayList<OfertaKupnaRobotnika> ok = new ArrayList<>();
        ArrayList<OfertaSprzedazyRobotnika> os = new ArrayList<>();
        if (!zyje)
        {
            return new Pair<>(ok, os);
        }
        if (!uczenie.czyPracuje(diamenty, dzien, SrednieCeny))
        {
            // Nauka
            dniBezJedzenia = 0;
            if (zmiana.czyZmieniamKariere(dzien))
            {
                // Robotnik zmienia sciezke kariery.
                aktualnaKariera = zmiana.zmienKariere(dzien, id, iloscPrzedmiotow, aktualnaKariera);
            }
            else
            {
                // Robotnik sie uczy.
                dodajPoziom();
            }
            return new Pair<>(ok, os);
        }
        else
        {
            // Robotnik pracuje.
            Ptyp p = produkcja.Produkuj(SrednieCeny, dzien);
            // Wystawia przedmioty na sprzedaz.
            OfertaSprzedazyRobotnika osr = new OfertaSprzedazyRobotnika(p, poziomWyprodukowanegoPrzedmiotu(p),
                    iloscProdkucji(p, kara_za_brak_ubran), id, getProdukt(1, Ptyp.diament), this);
            os.add(osr);
            // Wystawia oferty kupna.
            ok = kupowanie.kup(id, getProdukt(1, Ptyp.diament), iloscUbran(),
                    (int) iloscProdkucji(p, kara_za_brak_ubran), );
            // Sprawdzanie czy ma wystarczajaco dużo jedzenia.
            if (getProdukt(1, Ptyp.jedzenie) < 100)
            {
                // Robotnik ma za mało jedzenia.
                if (dniBezJedzenia >= 2)
                {
                    zyje = false;
                }
                dniBezJedzenia++;
            }
        }
        return new Pair<>(ok, os);
    }
    private int poziomKariery(Ztyp k)
    {
        return poziomyKarier.getOrDefault(k, 1);
    }
    private void dodajPoziom()
    {
        poziomyKarier.put(aktualnaKariera, poziomKariery(aktualnaKariera) + 1);
    }
    private double iloscProdkucji(Ptyp p, int kara_za_brak_ubran)
    {
        double w = produktywnosc.getOrDefault(p, 0);
        return w * Premia(p, kara_za_brak_ubran);
    }
    private int Premia(Ptyp p, int kara_za_brak_ubran)
    {
        int w = produktywnosc.getOrDefault(p, 0);
        if (produktNaZawod(p) == aktualnaKariera)
        {
            int poziom = poziomKariery(aktualnaKariera);
            switch (poziom) {
                case 1 -> w += 50;
                case 2 -> w += 150;
                case 3 -> w += 300;
                default -> w += 300 + (25 * (poziom - 3));
            }
        }
        switch (dniBezJedzenia){
            case 1 -> w -= 100;
            case 2 -> w -= 300;
        }
        if (iloscUbran() < 100)
        {
            w -= kara_za_brak_ubran;
        }
        w += produktywnoscNarzedzi();
        return w;
    }
    private Ptyp zawodNaProdukt(Ztyp z)
    {
        return switch (z) {
            case rolnik -> Ptyp.jedzenie;
            case rzemieslnik -> Ptyp.ubranie;
            case gornik -> Ptyp.diament;
            case inzynier -> Ptyp.narzedzie;
            default -> Ptyp.program;
        };
    }
    private Ztyp produktNaZawod(Ptyp p)
    {
        return switch (p) {
            case jedzenie -> Ztyp.rolnik;
            case diament -> Ztyp.gornik;
            case program -> Ztyp.programista;
            case ubranie -> Ztyp.rzemieslnik;
            default -> Ztyp.inzynier;
        };
    }
    private int iloscUbran()
    {
        int w = 0;
        for (Pair<Ptyp, Integer> ptypIntegerPair : produkty.keySet()) {
            if (ptypIntegerPair.getFirst() == Ptyp.ubranie)
            {
                w += (int) getProdukt(ptypIntegerPair.getSecond(), ptypIntegerPair.getFirst());
            }
        }
        return w;
    }
    private int poziomWyprodukowanegoPrzedmiotu(Ptyp p)
    {
        if (zawodNaProdukt(aktualnaKariera) == p)
        {
            return poziomKariery(aktualnaKariera);
        }
        // Jeszcze trzeba używać programów.
        return 1;
    }
    private int produktywnoscNarzedzi()
    {
        int w = 0;
        for (Pair<Ptyp, Integer> ptypIntegerPair : produkty.keySet()) {
            if (ptypIntegerPair.getFirst() == Ptyp.narzedzie)
            {
                w += ptypIntegerPair.getSecond() * (int) getProdukt(ptypIntegerPair.getSecond(), ptypIntegerPair.getFirst());
            }
        }
        return w;
    }
}


