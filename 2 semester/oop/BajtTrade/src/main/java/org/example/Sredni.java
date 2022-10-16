package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Sredni extends Spekulant{
    private final int historia_spekulanta_sredniego;
    public Sredni(int id, Map<Pair<Ptyp, Integer>, Integer> produkty, int h)
    {
        super(id, produkty);
        historia_spekulanta_sredniego = h;
    }
    @Override
    public ArrayList<OfertaKupnaSpekulanta> wystawOfertyKupna(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaKupnaSpekulanta> oferty = new ArrayList<>();
        for (int i = 1; i <= dzien; i++)
        {
            for (Ptyp p: Ptyp.values())
            {
                double cena = 0.9 * sredniaCenaHistoria(srednieCeny, dzien, p);
                OfertaKupnaSpekulanta o = new OfertaKupnaSpekulanta(p, i, 100, cena);
                oferty.add(o);
            }
        }
        return oferty;
    }

    @Override
    public ArrayList<OfertaSprzedazySpekulanta> wystawOfertySprzedazy(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaSprzedazySpekulanta> oferty = new ArrayList<>();
        for (Pair<Ptyp, Integer> p : produkty.keySet()) {
                double cena = 1.1 * sredniaCenaHistoria(srednieCeny, dzien, p.getFirst());
                OfertaSprzedazySpekulanta o = new OfertaSprzedazySpekulanta(p.getFirst(),
                        getProdukt(p.getSecond(), p.getFirst()), cena, p.getSecond());
                oferty.add(o);
        }
        return oferty;
    }
    private double sredniaCenaHistoria(Map<Pair<Ptyp, Integer>, Double> srednieCeny, int dzien, Ptyp p)
    {
        double w = 0;
        for (int i = Math.max(0, dzien - historia_spekulanta_sredniego); i < dzien; i++)
        {
            w += srednieCeny.getOrDefault(new Pair<>(p, i), (double)0);
        }
        w = w / (dzien - Math.max(0, dzien - historia_spekulanta_sredniego));
        return w;
    }
}