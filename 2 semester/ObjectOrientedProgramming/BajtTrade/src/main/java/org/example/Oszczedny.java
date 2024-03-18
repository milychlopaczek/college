package org.example;

import kotlin.Pair;

import java.util.HashMap;

public class Oszczedny extends StrategiaDnia{
    private double limit_diamentow;
    
    public Oszczedny(double ld)
    {
        limit_diamentow = ld;
    }
    @Override
    public boolean czyPracuje(double diamenty, int dzien, HashMap<Pair<Ptyp, Integer>, Double> srednieCeny)
    {
        return diamenty > limit_diamentow;
    }

    public double getLimit_diamentow() {
        return limit_diamentow;
    }
}
