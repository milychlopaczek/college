package org.example;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import kotlin.Pair;

import java.util.*;


public class Main {
    public static void main(String[] args) {
        // Moshi
        try {
            Moshi moshi = new Moshi.Builder().
                    add(new StratKupowanieAdapter()).
                    add(new StrategiaNaukiAdapter()).
                    add(new StrategiaProdukcjiAdapter()).
                    add(new GieldaAdapter()).
                    add(new StrategiaDniaAdapter()).
                    build();
            // moshi jest gotowe do wczytywania plikow json i wypisywania plikow json.
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}