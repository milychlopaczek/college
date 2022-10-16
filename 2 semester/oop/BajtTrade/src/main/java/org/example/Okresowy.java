package org.example;

import kotlin.Pair;

import java.util.HashMap;

public class Okresowy extends StrategiaDnia{
    private final int okresowosc_nauki;
    public Okresowy(int on)
    {
        okresowosc_nauki = on;
    }

    @Override
    public boolean czyPracuje(double diamenty, int dzien, HashMap<Pair<Ptyp, Integer>, Double> srednieCeny) {
        return dzien % okresowosc_nauki == 0;
    }

    public int getOkresowosc_nauki() {
        return okresowosc_nauki;
    }
}
