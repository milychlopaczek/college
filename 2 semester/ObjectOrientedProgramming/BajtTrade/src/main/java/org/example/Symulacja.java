package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class Symulacja {
    private HashMap<Pair<Ptyp, Integer>, Double> srednieCeny;
    HashMap<Pair<Ptyp, Integer>, Integer> iloscPrzedmiotow; // Mapa zwracająca ilosc przedmiotu na runku w danym dniu.
    private Gielda gielda;
    private ArrayList<Robotnik> robotnicy;
    private ArrayList<Spekulant> spekulanci;
    public Symulacja(Gielda g, HashMap<Pair<Ptyp, Integer>, Double> c, ArrayList<Robotnik> r, ArrayList<Spekulant> s)
    {
        gielda = g;
        srednieCeny = c;
        robotnicy = r;
        spekulanci = s;
        iloscPrzedmiotow = new HashMap<>();
        for (Ptyp p: Ptyp.values())
        {
            iloscPrzedmiotow.put(new Pair<>(p, 0), 1);
        }
    }
    public void przeprowadzSymulacje()
    {
        int dzien = 1;
    }
    public void przeprowadzDzien(int dzien, int kara_za_brak_ubran)
    {
        // Przetwarzanie Robotnikow.
        ArrayList<OfertaSprzedazyRobotnika> RS = new ArrayList<>();
        ArrayList<OfertaKupnaRobotnika> RK = new ArrayList<>();
        for (Robotnik robotnik : robotnicy) {
            RS.addAll(robotnik.przeprowadzDzien(dzien, iloscPrzedmiotow, srednieCeny, kara_za_brak_ubran).getSecond());
            RK.addAll(robotnik.przeprowadzDzien(dzien, iloscPrzedmiotow, srednieCeny, kara_za_brak_ubran).getFirst());
        }
        // Oferty Spekulantow.
        ArrayList<OfertaSprzedazySpekulanta> SS = new ArrayList<>();
        ArrayList<OfertaKupnaSpekulanta> SK = new ArrayList<>();
        for (Spekulant spekulant : spekulanci) {
            ArrayList<OfertaSprzedazySpekulanta> SSi = spekulant.wystawOfertySprzedazy(dzien, srednieCeny);
            ArrayList<OfertaKupnaSpekulanta> SKi = spekulant.wystawOfertyKupna(dzien, srednieCeny);
            SS.addAll(SSi);
            SK.addAll(SKi);
        }
        // Sortowanie ofert na Gieldzie.
        gielda.posortujOferty(RS, RK, dzien);
        // Logistyka Ofert na Gieldzie.
        for (OfertaKupnaRobotnika o: RK)
        {

        }
    }
}

