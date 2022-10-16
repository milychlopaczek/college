package org.example;

import java.util.ArrayList;

public class Zrownowazona extends Gielda{
    @Override
    public void posortujOferty(ArrayList<OfertaSprzedazyRobotnika> RS, ArrayList<OfertaKupnaRobotnika> RK, int dzien) {
        if (dzien % 2 == 1)
        {
            Socjalistyczna s = new Socjalistyczna;
            s.posortujOferty(RS, RK, dzien);
        }
        else
        {
            Kapitalistyczna s = new Kapitalistyczna();
            s.posortujOferty(RS, RK, dzien);
        }
    }
}
