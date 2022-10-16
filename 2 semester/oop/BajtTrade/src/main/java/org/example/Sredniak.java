package org.example;

import kotlin.Pair;

import java.util.Map;

public class Sredniak extends StrategiaProdukcji{

    private final int historia_sredniej_produkcji;
    public Sredniak(int hsp)
    {
        historia_sredniej_produkcji = hsp;
    }
    @Override
    public Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien) {
        Ptyp wynik = null;
        double max = 0;
        for (int i = Math.max(dzien - historia_sredniej_produkcji, 0); i < dzien; i++)
        {
            for (Ptyp t: Ptyp.values())
            {
                double r = sredniaCena.getOrDefault(new Pair<>(t, i), (double)0);
                if (r >= max)
                {
                    wynik = t;
                    max = r;
                }
            }
        }
        return wynik;
    }

    public int getHistoria_sredniej_produkcji() {
        return historia_sredniej_produkcji;
    }
}
