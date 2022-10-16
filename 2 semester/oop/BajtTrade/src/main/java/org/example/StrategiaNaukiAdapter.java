package org.example;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class StrategiaNaukiAdapter {
    @ToJson
    Object ToJson(StrategiaKariery kariera)
    {
        Object json;
        if (kariera.getClass() == Konserwatysta.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "konserwatysta";
        }
        else if (kariera.getClass() == Rewolucjonista.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "rewolucjonista";
        }
        else {
            throw new JsonDataException("nieznany obiekt");
        }
        return json;
    }
    @FromJson
    StrategiaKariery naukaFromJson(BezArgAdapter nauka) {
        return switch (nauka.nazwa) {
            case "konserwatysta" -> new Konserwatysta();
            default -> new Rewolucjonista();
        };
    }
}
