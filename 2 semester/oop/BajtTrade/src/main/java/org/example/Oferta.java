package org.example;

public abstract class Oferta {
    private Ptyp produkt;
    private double ilosc;
    public Oferta(Ptyp p, double i)
    {
        produkt = p;
        ilosc = i;
    }

    public double getIlosc() {
        return ilosc;
    }

    public Ptyp getProdukt() {
        return produkt;
    }
}
