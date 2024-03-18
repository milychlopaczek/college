package org.example;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.ToJson;

public class StrategiaDniaAdapter {
        @ToJson
        Object ToJson(StrategiaDnia dzien)
        {
            Object json;
            if (dzien.getClass() == Pracus.class)
            {
                json = new BezArgAdapter();
                ((BezArgAdapter) json).nazwa = "pracus";
            }
            else if (dzien.getClass() == Oszczedny.class)
            {
                json = new dzienAdapter();
                ((dzienAdapter) json).nazwa = "oszczedny";
                ((dzienAdapter) json).limit_diamentow = ((Oszczedny) dzien).getLimit_diamentow();
            }
            else if (dzien.getClass() == Student.class)
            {
                json = new dzienAdapter();
                ((dzienAdapter) json).nazwa = "student";
                ((dzienAdapter) json).okres = ((Student) dzien).getOkres();
                ((dzienAdapter) json).zapas = ((Student) dzien).getZapas();
            }
            else if (dzien.getClass() == Okresowy.class)
            {
                json = new dzienAdapter();
                ((dzienAdapter) json).nazwa = "okresowy";
                ((dzienAdapter) json).okres = ((Okresowy) dzien).getOkresowosc_nauki();
            }
            else if (dzien.getClass() == Rozkladowy.class)
            {
                json = new BezArgAdapter();
                ((BezArgAdapter) json).nazwa = "rozkladowy";
            }
            else {
                throw new JsonDataException("nieznany obiekt");
            }
            return json;
        }
        @FromJson
        StrategiaDnia dzienFromJson(dzienAdapter dzien) {
            return switch (dzien.nazwa) {
                case "pracus" -> new Pracus();
                case "oszczedny" -> new Oszczedny(dzien.limit_diamentow);
                case "student" -> new Student(dzien.zapas, dzien.okres);
                case "okresowy" -> new Okresowy(dzien.okresowosc_nauki);
                default -> new Rozkladowy();
            };
        }
}


