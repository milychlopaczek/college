package org.example;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class GieldaAdapter {
    @ToJson
    Object ToJson(Gielda g)
    {
        Object json;
        if (g.getClass() == Kapitalistyczna.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "kapitalistyczna";
        }
        else if (g.getClass() == Socjalistyczna.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "socjalistyczna";
        }
        else if (g.getClass() == Zrownowazona.class)
        {
            json = new BezArgAdapter();
            ((BezArgAdapter) json).nazwa = "zrownowazona";
        }
        else {
            throw new JsonDataException("nieznany obiekt");
        }
        return json;
    }
    @FromJson
    Gielda gieldaFromJson(BezArgAdapter gielda) {
        return switch (gielda.nazwa) {
            case "kapitalistyczna" -> new Kapitalistyczna();
            case "socjalistyczna" -> new Socjalistyczna();
            default -> new Zrownowazona();
        };
    }
}
