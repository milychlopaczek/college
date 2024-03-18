package org.example;

import kotlin.Pair;

import java.util.Map;

public class Krotkowzroczny extends StrategiaProdukcji {
    @Override
    public Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien) {
        double max = 0;
        Ptyp wynik = null;
        for (Ptyp t: Ptyp.values())
        {
            double r = sredniaCena.getOrDefault(new Pair<>(t, dzien - 1), (double)0);
            if (r >= max)
            {
                max = r;
                wynik = t;
            }
        }
        return wynik;
    }
}
