package org.example;


import java.util.ArrayList;

public abstract class StrategiaKupowania {
    public StrategiaKupowania(){}
    public abstract ArrayList<OfertaKupnaRobotnika> kup(int idRobotnika, double diamentyRobotnika, int iloscUbran, int iloscProdukcji, Robotnik robotnik);

}
