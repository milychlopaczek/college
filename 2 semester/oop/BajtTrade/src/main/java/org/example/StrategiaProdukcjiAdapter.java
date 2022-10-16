package org.example;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class StrategiaProdukcjiAdapter {
    @ToJson
    Object ToJson(StrategiaProdukcji produkcja)
    {
        Object json;
        if (produkcja.getClass() == Krotkowzroczny.class)
        {
            json = new ProdukcjaAdapter();
            ((ProdukcjaAdapter) json).nazwa = "krotokowzroczny";
        }
        else if (produkcja.getClass() == Chciwy.class)
        {
            json = new ProdukcjaAdapter();
            ((ProdukcjaAdapter) json).nazwa = "chciwy";
        }
        else if (produkcja.getClass() == Sredniak.class)
        {
            json = new ProdukcjaAdapter();
            ((ProdukcjaAdapter) json).nazwa = "sredniak";
            ((ProdukcjaAdapter) json).historia_sredniej_produkcji = ((Sredniak) produkcja).getHistoria_sredniej_produkcji();
        }
        else if (produkcja.getClass() == Perspektywiczny.class)
        {
            json = new ProdukcjaAdapter();
            ((ProdukcjaAdapter) json).nazwa = "perpektywiczny";
            ((ProdukcjaAdapter) json).historia_perspektywy = ((Perspektywiczny) produkcja).getHistoria_perspektywy();
        }
        else {
            throw new JsonDataException("nieznany obiekt");
        }
        return json;
    }
    @FromJson
    StrategiaProdukcji produkcjaFromJson(ProdukcjaAdapter kupowanie) {
        return switch (kupowanie.nazwa) {
            case "krotkowzroczny" -> new Krotkowzroczny();
            case "chciwy" -> new Chciwy();
            case "sredniak" -> new Sredniak(kupowanie.historia_sredniej_produkcji);
            default -> new Perspektywiczny(kupowanie.historia_perspektywy);
        };
    }
}
