package org.example;

import kotlin.Pair;

import java.util.HashMap;

public abstract class Agent {
    protected int id;
    protected HashMap<Pair<Ptyp, Integer>, Double> produkty;
    public Agent(int id, HashMap<Pair<Ptyp, Integer>, Double> produkty)
    {
        this.id = id;
        this.produkty = produkty;
    }

    public int getId() {
        return id;
    }
    public double getProdukt(int arg, Ptyp p)
    {
        return produkty.getOrDefault(new Pair(p, arg), (double)0);
    }
}
