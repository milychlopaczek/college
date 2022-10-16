package org.example;

import kotlin.Pair;

import java.util.HashMap;

public class Rewolucjonista extends StrategiaKariery{
    @Override
    public Ztyp zmienKariere(int dzien, int id, HashMap<Pair<Ptyp, Integer>, Integer> iloscPrzedmiotow, Ztyp aktualnaKariera) {
        int n =  Math.max(1, id % 17);
        Ptyp w = null;
        int mx = 0;
        for (Ptyp p: Ptyp.values())
        {
            int il = 0;
            for (int i = Math.max(1, dzien - n); i < dzien; i++)
            {
                il += iloscPrzedmiotow.getOrDefault(new Pair<>(p, i), 0);
            }
            if (mx < il)
            {
                mx = il;
                w = p;
            }
        }
        return karieraZPrzedmiotu(w);
    }
    private Ztyp karieraZPrzedmiotu(Ptyp p)
    {
        return switch (p) {
            case jedzenie -> Ztyp.rolnik;
            case diament -> Ztyp.gornik;
            case program -> Ztyp.programista;
            case ubranie -> Ztyp.rzemieslnik;
            default -> Ztyp.inzynier;
        };
    }
    @Override
    public boolean czyZmieniamKariere(int dzien) {
        return dzien % 7 == 0;
    }
}
