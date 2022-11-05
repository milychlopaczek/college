package org.example;

public class Produkt {
    Ptyp nazwa;
    private int poziom;
    public Produkt(Ptyp t, int poziom)
    {
        this.poziom = poziom;
        nazwa = t;
    }

    public Ptyp getNazwa() {
        return nazwa;
    }

    public int getPoziom() {
        return poziom;
    }

}

