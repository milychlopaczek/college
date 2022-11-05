package org.example;

import kotlin.Pair;

import java.util.Map;

public abstract class StrategiaProdukcji {
    public abstract Ptyp Produkuj(Map<Pair<Ptyp, Integer>, Double> sredniaCena, int dzien);
}

