package org.example;

import java.util.ArrayList;

public class Technofob extends StrategiaKupowania{
    public Technofob() {}

    @Override
    public ArrayList<OfertaKupnaRobotnika> kup(int idRobotnika, double diamentyRobotnika, int iloscUbran, int iloscProgramow, Robotnik robotnik) {
        ArrayList<OfertaKupnaRobotnika> o = new ArrayList<>();
        o.add(new OfertaKupnaRobotnika(Ptyp.jedzenie, 100, idRobotnika, diamentyRobotnika, robotnik));
        return o;
    }
}
