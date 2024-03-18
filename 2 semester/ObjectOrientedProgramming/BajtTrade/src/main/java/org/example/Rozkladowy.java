package org.example;

import kotlin.Pair;

import java.util.HashMap;

public class Rozkladowy extends StrategiaDnia{
    @Override
    public boolean czyPracuje(double diamenty, int dzien, HashMap<Pair<Ptyp, Integer>, Double> srednieCeny) {
        double rand = Math.random();
        double prawdopodobienstwo = 1 - (1/(dzien + 3)); // Prawdopodobienstwo na pracę.
        return rand <= prawdopodobienstwo;
    }
}


