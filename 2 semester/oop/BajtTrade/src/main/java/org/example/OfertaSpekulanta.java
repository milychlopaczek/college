package org.example;

public abstract class OfertaSpekulanta extends Oferta{
    private double cena;
    private int poziom;
    OfertaSpekulanta(Ptyp p, double i, double c, int poziom)
    {
        super(p, i);
        cena = c;
        this.poziom = poziom;
    }

    public double getCena() {
        return cena;
    }

    public int getPoziom() {
        return poziom;
    }
}
