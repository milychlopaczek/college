package org.example;

import java.util.ArrayList;

public class Czyscioszek extends Technofob{
    public Czyscioszek() {}
    @Override
    public ArrayList<OfertaKupnaRobotnika> kup(int idRobotnika, double diamentyRobotnika, int iloscUbran, int iloscProgramow, Robotnik robotnik) {
        OfertaKupnaRobotnika o = new OfertaKupnaRobotnika(Ptyp.ubranie, 100, idRobotnika, diamentyRobotnika, robotnik);
        ArrayList<OfertaKupnaRobotnika> w = super.kup(idRobotnika, diamentyRobotnika, iloscUbran, iloscProgramow, robotnik);
        w.add(o);
        return w;
    }
}
