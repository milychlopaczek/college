package org.example;

import kotlin.Pair;

import java.util.HashMap;

public abstract class StrategiaKariery {
    public Ztyp zmienKariere(int dzien, int id, HashMap<Pair<Ptyp, Integer>, Integer> iloscPrzedmiotow, Ztyp aktualnaKariera)
    {
        return aktualnaKariera;
    }
    public boolean czyZmieniamKariere(int dzien)
    {
        return false;
    }
}


