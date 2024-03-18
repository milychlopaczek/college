package org.example;

import kotlin.Pair;

import java.util.HashMap;

public class Student extends StrategiaDnia{
    private double zapas;
    private int okres;
    public Student(double z, int o)
    {
        okres = o;
        zapas = z;
    }
    @Override
    public boolean czyPracuje(double diamenty, int dzien, HashMap<Pair<Ptyp, Integer>, Double> srednieCeny)
    {
        double cena = 0;
        for (Ptyp p: Ptyp.values())
        {
            double sredniaCena = 0;
            for (int i = Math.max(1, dzien - okres); i < dzien; i++)
            {
                sredniaCena += srednieCeny.getOrDefault(new Pair<>(p, i), (double)0);
            }
            sredniaCena = sredniaCena / (dzien - Math.max(1, dzien - okres));
            cena += sredniaCena * 100 * zapas;
        }
        return cena <= diamenty;
    }

    public double getZapas() {
        return zapas;
    }

    public int getOkres() {
        return okres;
    }
}

