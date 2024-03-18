package org.example;

import kotlin.Pair;

import java.util.ArrayList;
import java.util.Map;

public abstract class Spekulant extends Agent{
    public Spekulant(int id, Map<Pair<Ptyp, Integer>, Integer> produkty)
    {
        super(id, produkty);
    }
    public abstract ArrayList<OfertaKupnaSpekulanta> wystawOfertyKupna(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny);
    public abstract ArrayList<OfertaSprzedazySpekulanta> wystawOfertySprzedazy(int dzien, Map<Pair<Ptyp, Integer>, Double> srednieCeny);
}


