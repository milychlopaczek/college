package org.example;

public abstract class OfertaRobotnika extends Oferta{
    protected int idRobotnika;
    protected double diamentyRobotnika;
    protected Robotnik robotnik;
    
    public OfertaRobotnika(Ptyp p, double i, int id, double d, Robotnik r)
    {
        super(p, i);
        idRobotnika = id;
        diamentyRobotnika = d;
        robotnik = r;
    }

    public int getIdRobotnika() {
        return idRobotnika;
    }

    public double getDiamentyRobotnika() {
        return diamentyRobotnika;
    }

    public Robotnik getRobotnik() {
        return robotnik;
    }
}
