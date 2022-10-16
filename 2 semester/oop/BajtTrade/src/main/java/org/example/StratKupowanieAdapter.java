package org.example;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class StratKupowanieAdapter{
    @ToJson
    Object ToJson(StrategiaKupowania kupowanie)
    {
        Object json;
        if (kupowanie.getClass() == Technofob.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "technofob";
        }
        else if (kupowanie.getClass() == Czyscioszek.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "czyscioszek";
        }
        else if (kupowanie.getClass() == Zmechanizowany.class)
        {
            json = new KupowanieAdapter();
            ((KupowanieAdapter) json).nazwa = "zmechanizowany";
            ((KupowanieAdapter) json).liczba_narzedzi = ((Zmechanizowany) kupowanie).getLiczba_narzedzi();
        }
        else if (kupowanie.getClass() == Gadzeciarz.class)
        {
            json = new KupowanieAdapter();
            ((KupowanieAdapter) json).nazwa = "gadzeciarz";
            ((KupowanieAdapter) json).liczba_narzedzi = ((Gadzeciarz) kupowanie).getLiczba_narzedzi();
        }
        else {
            throw new JsonDataException("nieznany obiekt");
        }
        return json;
    }
    @FromJson StrategiaKupowania kupowanieFromJson(KupowanieAdapter strategiaKupowaniaJsonAdapter) {
        return switch (strategiaKupowaniaJsonAdapter.nazwa) {
            case "technofob" -> new Technofob();
            case "czyscioszek" -> new Czyscioszek();
            case "zmechanizowany" -> new Zmechanizowany(strategiaKupowaniaJsonAdapter.liczba_narzedzi);
            default -> new Gadzeciarz(strategiaKupowaniaJsonAdapter.liczba_narzedzi);
        };
    }
}
