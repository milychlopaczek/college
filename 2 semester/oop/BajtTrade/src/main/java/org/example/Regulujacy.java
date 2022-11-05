package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.Map;

public class Regulujacy extends Spekulant{
    public Regulujacy(int id, Map<Pair<Ptyp, Integer>, Integer> produkty)
    {
        super(id, produkty);
    }

    @Override
    public ArrayList<OfertaKupnaSpekulanta> wystawOfertyKupna(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaKupnaSpekulanta> oferty = new ArrayList<>();
        if (dzien == 1)
        {
            return oferty;
        }
        for (int i = 1; i <= dzien; i++)
        {
            for (Ptyp p: Ptyp.values())
            {
                double cena = 0.9 * nowaCena(srednieCeny, dzien, p);
                OfertaKupnaSpekulanta o = new OfertaKupnaSpekulanta(p, 1, 100, cena);
                oferty.add(o);
            }
        }
        return oferty;
    }

    @Override
    public ArrayList<OfertaSprzedazySpekulanta> wystawOfertySprzedazy(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny) {
        ArrayList<OfertaSprzedazySpekulanta> oferty = new ArrayList<OfertaSprzedazySpekulanta>();
        if (dzien == 1)
        {
            return oferty;
        }
        for (Pair<Ptyp, Integer> p : produkty.keySet()) {
            double cena = 1.1 * nowaCena(srednieCeny, dzien, p.getFirst());
            OfertaSprzedazySpekulanta o = new OfertaSprzedazySpekulanta(p.getFirst(),
                    getProdukt(p.getSecond(), p.getFirst()), cena, p.getSecond());
            oferty.add(o);
        }
        return oferty;
    }
    private double nowaCena(Map<Pair<Ptyp, Integer>, Double> srednieCeny, int dzien, Ptyp p)
    {
        double stala, w;
        stala = 1; // Trzeba zmienic wg wzoru.
        w = srednieCeny.getOrDefault(new Pair<>(p, dzien - 1), (double)0);
        w = w * stala;
        return w;
    }
}

