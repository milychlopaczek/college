package org.example;

import java.util.ArrayList;

public class Zmechanizowany extends Czyscioszek{
    protected int liczba_narzedzi;
    public Zmechanizowany(int ln)
    {
        liczba_narzedzi = ln;
    }

    public int getLiczba_narzedzi() {
        return liczba_narzedzi;
    }
    @Override
    public ArrayList<OfertaKupnaRobotnika> kup(int idRobotnika, double diamentyRobotnika, int iloscUbran, int iloscProgramow, Robotnik robotnik) {
        OfertaKupnaRobotnika o = new OfertaKupnaRobotnika(Ptyp.narzedzie, liczba_narzedzi, idRobotnika, diamentyRobotnika, robotnik);
        ArrayList<OfertaKupnaRobotnika> w = super.kup(idRobotnika, diamentyRobotnika, iloscUbran, iloscProgramow, robotnik);
        w.add(o);
        return w;
    }
}
