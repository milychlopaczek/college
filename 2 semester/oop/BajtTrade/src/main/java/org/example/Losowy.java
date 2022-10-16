package org.example;

import kotlin.Pair;

import java.util.Map;

public class Losowy extends StrategiaProdukcji{
    @Override
    public Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien) {
        double s = Math.random();
        if (s <= 0.25)
        {
            return Ptyp.jedzenie;
        }
        else if (s <= 0.5)
        {
            return Ptyp.ubranie;
        }
        else if (s <= 0.75)
        {
            return Ptyp.narzedzie;
        }
        return Ptyp.program;
    }
}
