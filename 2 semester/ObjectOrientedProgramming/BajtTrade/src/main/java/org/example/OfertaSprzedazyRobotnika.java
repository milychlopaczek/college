package org.example;

public class OfertaSprzedazyRobotnika extends OfertaRobotnika{
    private int poziom;
    
    public OfertaSprzedazyRobotnika(Ptyp p, int poziom, double i, int id, double d, Robotnik r)
    {
        super(p, i, id, d, r);
        this.poziom = poziom;
    }

    public int getPoziom() {
        return poziom;
    }
}
