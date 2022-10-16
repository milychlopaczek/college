package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Socjalistyczna extends Gielda{
    @Override
    public void posortujOferty(ArrayList<OfertaSprzedazyRobotnika> RS, ArrayList<OfertaKupnaRobotnika> RK, int dzien) {
        Collections.sort(RS, Comparator.comparing(OfertaRobotnika::getDiamentyRobotnika).thenComparing(OfertaRobotnika::getIdRobotnika));
        Collections.sort(RK, Comparator.comparing(OfertaRobotnika::getDiamentyRobotnika).thenComparing(OfertaRobotnika::getIdRobotnika));
    }
}
