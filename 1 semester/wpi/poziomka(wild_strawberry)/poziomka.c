#include <stdio.h>
#include <stdbool.h>

#ifndef POLA
#define POLA 5
#endif
#ifndef WIERSZE
#define WIERSZE 26
#endif
#ifndef KOLUMNY
#define KOLUMNY 26
#endif
#ifndef WYBOR
#define WYBOR 1000
#endif

struct para
{
    int w;
    int k;
};

int plansza[WIERSZE][KOLUMNY];

// Funkcja wczytująca jeden ruch gracza.
struct para wczytywanie_ruchu()
{
    int wiersz = 0;
    int kolumna = getchar();
    while ((kolumna < 'A' || kolumna > 'Z') && kolumna != '-' && kolumna != '.')
    {
        kolumna = getchar();
    }
    if (kolumna >= 'A' && kolumna <= 'Z')
    {
        wiersz = getchar();
    }
    else
    {
        wiersz = -1;
    }
    struct para ruch;
    ruch.k = kolumna;
    ruch.w = wiersz;
    return ruch;
}

// Funkcja zwracająca nową wartość oceny wiersza po ewentualnym ruchu bota.
static int nowa_ocena_wiersza(const int wiersz, const int bot_kolumna)
{
    int i = 0;
    int poziomki = 0;   // Liczba poziomek w danym wierszu.
    int suma = 0;   // Długość aktualnego spójnego podciągu '.' w planszy (od i-suma do i).
    while (i < KOLUMNY)
    {
        if ((wiersz < WIERSZE && plansza[wiersz][i] == '#') || (i >= bot_kolumna && i < bot_kolumna + POLA))
        {
            poziomki = poziomki + (suma / POLA);
            suma = 0;
        }
        else
        {
            suma++;
        }
        i++;
    }
    poziomki = poziomki + (suma / POLA);
    return poziomki;
}

// Funkcja zwracająca nową wartość oceny kolumny po ewentualnym ruchu bota
static int nowa_ocena_kolumny(const int kolumna, const int bot_wiersz)
{
    int i = 0;
    int poziomki = 0;   // Liczba poziomek w danej kolumnie.
    int suma = 0;   // Długość aktualnego spójnego podciągu '.' w planszy (od i-suma do i).
    while (i < WIERSZE)
    {
        if ((kolumna < KOLUMNY && plansza[i][kolumna] == '#') || bot_wiersz == i)
        {
            poziomki = poziomki + (suma / POLA);
            suma = 0;
        }
        else
        {
            suma++;
        }
        i++;
    }
    poziomki = poziomki + (suma / POLA);
    return poziomki;
}

// Funkcja aktualizująca tablicę plansza po ruchu.
static void aktualizacja_planszy(const struct para ruch, const bool pionowy)
{
    int kolumna = ruch.k - 'A';
    int wiersz = ruch.w - 'a';
    for (int h = 0; h < POLA; h++)
    {
        if (pionowy)
        {
            plansza[wiersz + h][kolumna] = '#';
        }
        else
        {
            plansza[wiersz][kolumna + h] = '#';
        }
    }
}

// Funkcja szukająca najlepszego ruchu bota.
static struct para szukanie_najlepszego_ruchu(const int ocena_wierszy[], const int ocena_kolumn[])
{
    struct para najlepsze_ruchy[WIERSZE * KOLUMNY];   // Tablica przechowująca najlepsze ruchy.
    int max = - (WIERSZE * KOLUMNY);    // Zmienna przechowująca maksymalną zmianę oceny planszy.
    int ilosc = 0;
    for (int h = 0; h < WIERSZE; h++)
    {
        int l = 0;
        int p = 0;
        while (l <= (KOLUMNY - POLA) && p <= KOLUMNY)
        {
            if(p - l >= POLA)
            {
                int ocena = 0;  // Różnica oceny tablicy po ruchu h,l.
                ocena += (nowa_ocena_wiersza(h, l) - ocena_wierszy[h]);
                for (int g = 0; g < POLA; g++)
                {
                    ocena += (ocena_kolumn[l + g] - nowa_ocena_kolumny(l + g, h));
                }
                if (ocena > max)
                {
                    ilosc = 1;
                    najlepsze_ruchy[ilosc - 1].w = h;
                    najlepsze_ruchy[ilosc - 1].k = l;
                    max = ocena;
                }
                else if (ocena == max)
                {
                    najlepsze_ruchy[ilosc].w = h;
                    najlepsze_ruchy[ilosc].k = l;
                    ilosc++;
                }
                l++;
            }
            if (p < KOLUMNY && plansza[h][p] == '.')
            {
                p++;
            }
            else
            {
                p++;
                l = p;
            }
        }
    }
    struct para ruch;
    if (ilosc > 0)  // Zwracanie najlepszego ruchu bota.
    {
        ruch.w = najlepsze_ruchy[WYBOR % ilosc].w + 'a';
        ruch.k = najlepsze_ruchy[WYBOR % ilosc].k + 'A';
    }
    else
    {
        ruch.w = -1;
        ruch.k = -1;
    }
    return ruch;
}

void program(int ocena_kolumn[], int ocena_wierszy[])
{
    struct para ruch;
    ruch = wczytywanie_ruchu();
    int ocena;
    if (ruch.w >= 'a' && ruch.w <= 'z') // Aktualizowanie zmiennych po ruchu gracza.
    {
        aktualizacja_planszy(ruch, 1);
        ocena = nowa_ocena_kolumny(ruch.k - 'A', -1);
        ocena_kolumn[ruch.k - 'A'] = ocena;
        for (int h = 0; h < POLA; h++)
        {
            ocena = nowa_ocena_wiersza(ruch.w - 'a' + h, -POLA);
            ocena_wierszy[ruch.w - 'a' + h] = ocena;
        }
    }
    if (ruch.k == '-' || (ruch.k >= 'A' && ruch.k <= 'Z'))
    {
        struct para ruch_bota = szukanie_najlepszego_ruchu(ocena_wierszy, ocena_kolumn);
        if (ruch_bota.w == -1)   // Poddanie się bota.
        {
            printf(".\n");
        }
        else    // Aktualizacja zmiennych po ruchu bota.
        {
            printf("%c%c\n", ruch_bota.w, ruch_bota.k);
            aktualizacja_planszy(ruch_bota, 0);
            ocena = nowa_ocena_wiersza(ruch_bota.w - 'a', -POLA);
            ocena_wierszy[ruch_bota.w - 'a'] = ocena;
            for (int h = 0; h < POLA; h++)
            {
                ocena = nowa_ocena_kolumny(ruch_bota.k - 'A' + h, -1);
                ocena_kolumn[ruch_bota.k - 'A' + h] = ocena;
            }
            program(ocena_kolumn, ocena_wierszy);   // Kolejna kolejka ruchów.
        }
    }
}

int main()
{
    for (int h = 0; h < WIERSZE; h++)
    {
        for (int g = 0; g < KOLUMNY; g++)
        {
            plansza[h][g] = '.';
        }
    }
    int ocena_kolumn[KOLUMNY];
    int ocena_wierszy[WIERSZE];
    for (int h = 0; h < WIERSZE; h++)   //Inicjalizacja wartości startowych ocen wierszy.
    {
        ocena_wierszy[h] = KOLUMNY / POLA;
    }
    for (int h = 0; h < KOLUMNY; h++)   //Inicjalizacja wartości startowych ocen kolumn.
    {
        ocena_kolumn[h] = WIERSZE / POLA;
    }
    program(ocena_kolumn, ocena_wierszy);   //Wywołanie głównej funkcji.
}
