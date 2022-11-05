package org.example;

public class Kariera {
    private Ztyp nazwa;
    private int poziom;
    public Kariera(int p, Ztyp nazwa)
    {
        poziom = p;
        this.nazwa = nazwa;
    }
    
    public void dodajPoziom()
    {
        poziom++;
    }

    public int getPoziom() {
        return poziom;
    }

    public Ztyp getNazwa() {
        return nazwa;
    }
}
