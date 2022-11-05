package org.example;

import kotlin.Pair;

import java.util.Map;

public class Perspektywiczny extends StrategiaProdukcji{
    private int historia_perspektywy;
    public Perspektywiczny(int hp)
    {
        historia_perspektywy = hp;
    }
    
    @Override
    public Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien) {
        double roznica = Double.MIN_VALUE;
        Ptyp wynik = Ptyp.jedzenie;
        for (Ptyp t: Ptyp.values())
        {
            double r = sredniaCena.getOrDefault(new Pair<>(t, dzien - 1), (double)0);
            r -= sredniaCena.getOrDefault(new Pair<>(t, Math.max(dzien - historia_perspektywy, 1)), (double)0);
            if (r >= roznica)
            {
                roznica = r;
                wynik = t;
            }
        }
        return wynik;
    }

    public int getHistoria_perspektywy() {
        return historia_perspektywy;
    }
}
