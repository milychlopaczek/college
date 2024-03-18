package org.example;

import kotlin.Pair;

import java.util.Map;

public class Chciwy extends StrategiaProdukcji{

    @Override
    public Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien) {
        // Trzeba najpierw zakodzic premie itd.
        return Ptyp.jedzenie;
    }
}
