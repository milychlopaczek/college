package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Wypukly extends Spekulant{
    public Wypukly(int id, Map<Pair<Ptyp, Integer>, Integer> produkty)
    {
        super(id, produkty);
    }
    @Override
    public ArrayList<OfertaKupnaSpekulanta> wystawOfertyKupna(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaKupnaSpekulanta> oferty = new ArrayList<>();
        for (int i = 1; i <= dzien; i++)
        {
            for (Ptyp p: Ptyp.values())
            {
                if (wypukla(srednieCeny, dzien, p))
                {
                    double cena = 0.9 * srednieCeny.getOrDefault(new Pair<>(p, dzien - 1), (double)0);
                    OfertaKupnaSpekulanta o = new OfertaKupnaSpekulanta(p, i, 100, cena);
                    oferty.add(o);
                }
            }
        }
        return oferty;
    }

    @Override
    public ArrayList<OfertaSprzedazySpekulanta> wystawOfertySprzedazy(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaSprzedazySpekulanta> oferty = new ArrayList<>();
        for (Pair<Ptyp, Integer> p : produkty.keySet()) {
            if (wklesla(srednieCeny, dzien, p.getFirst()))
            {
                double cena = 1.1 * srednieCeny.getOrDefault(new Pair<>(p.getFirst(), dzien - 1), (double)0);
                OfertaSprzedazySpekulanta o = new OfertaSprzedazySpekulanta(p.getFirst(),
                        getProdukt(p.getSecond(), p.getFirst()), cena, p.getSecond());
                oferty.add(o);
            }
        }
        return oferty;
    }
    private boolean wypukla(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien, Ptyp p)
    {
        if (dzien < 4)
            return false;
        if (sredniaCena.getOrDefault(new Pair<>(p, dzien - 3), (double)0) >=
                sredniaCena.getOrDefault(new Pair<>(p, dzien - 2), (double)0))
        {
            return false;
        }
        else if (sredniaCena.getOrDefault(new Pair<>(p, dzien - 1), (double)0) >=
                sredniaCena.getOrDefault(new Pair<>(p, dzien - 2), (double)0))
        {
            return false;
        }
        return true;
    }
    private boolean wklesla(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien, Ptyp p)
    {
        if (dzien < 4)
            return false;
        if (sredniaCena.getOrDefault(new Pair<>(p, dzien - 3), (double)0) <=
                sredniaCena.getOrDefault(new Pair<>(p, dzien - 2), (double)0))
        {
            return false;
        }
        else if (sredniaCena.getOrDefault(new Pair<>(p, dzien - 1), (double)0) <=
                sredniaCena.getOrDefault(new Pair<>(p, dzien - 2), (double)0))
        {
            return false;
        }
        return true;
    }
}

