package org.example;

import java.util.ArrayList;

public class Gadzeciarz extends Zmechanizowany{
    public Gadzeciarz(int ln) {super(ln);}
    
    @Override
    public ArrayList<OfertaKupnaRobotnika> kup(int idRobotnika, double diamentyRobotnika, int iloscUbran, int iloscProgramow, Robotnik robotnik) {
        OfertaKupnaRobotnika o = new OfertaKupnaRobotnika(Ptyp.program, iloscProgramow, idRobotnika, diamentyRobotnika, robotnik);
        ArrayList<OfertaKupnaRobotnika> w = super.kup(idRobotnika, diamentyRobotnika, iloscUbran, iloscProgramow, robotnik);
        w.add(o);
        return w;
    }
}
