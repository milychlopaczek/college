#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

#define MAX_DEG 10
#define LINE_LENGTH 1000

struct para{
    int dzialanie;
    int tablica[11];
    int dlugosc;
};
static void wypisywanie_wsp(int wsp_wielomianu[], int najwiekszy_wspolczynnik)//funkcja wypisująca wielomian
{
    bool pierwsza = false; //zmienna potrzebna, aby nie wypisywać plusa przed pierwszym jednomianem. true: pierwszy jednomian zostal wypisany
    for (int h = najwiekszy_wspolczynnik; h >= 0; h--)
        {
            if (wsp_wielomianu[h]!=0)
            {
                if(pierwsza == false)
                {
                    if(wsp_wielomianu[h] < 0)
                    {
                        printf("-");
                    }
                }
                else
                {
                    if (wsp_wielomianu[h] > 0)
                    {
                        printf(" + ");
                    }
                    else if (wsp_wielomianu[h] < 0 )
                    {
                        printf(" - ");
                    }
                }
                if (wsp_wielomianu[h] == 1 || wsp_wielomianu[h] == -1)
                {
                    if (h == 0)
                        printf("%d", abs(wsp_wielomianu[h]));
                    else if (h == 1)
                        printf("x");
                    else
                        printf("x^%d", h);
                }
                else
                {
                if (h == 0)
                    printf("%d", abs(wsp_wielomianu[h]));
                else if (h == 1)
                    printf("%dx", abs(wsp_wielomianu[h]));
                else
                    printf("%dx^%d", abs(wsp_wielomianu[h]), h);
                }
                pierwsza = true;
            }
        }
    if(pierwsza == false) printf("0"); // jeśli wielomian to samo zero
    printf("\n");
}
static void dodawanie(int wsp_wielomianu[], int tablica_wynikowa[], int dlugosc_wielomianu)
{
    for (int h = 0; h <= dlugosc_wielomianu; h++) tablica_wynikowa[h] += wsp_wielomianu[h];
}
static void mnozenie(int wsp_wielomianu[], int tablica_wynikowa[], int dlugosc_wielomianu)
{
    int pom[2*MAX_DEG+1];
    for (int h = 0; h <= 2*MAX_DEG; h++) pom[h] = 0;

    for (int h = 0; h <= dlugosc_wielomianu; h++)
        for (int g = 0; g <= MAX_DEG; g++)
            pom[h + g] += tablica_wynikowa[g] * wsp_wielomianu[h];

    for (int h = 0; h <= MAX_DEG; h++) tablica_wynikowa[h] = pom[h];
}
static int konwersja(int linia[], int wsp_wielomianu[], int dlugosc)//konwerter ciagu znakow na wspolczynniki wielomianu
{
    int wsp = 0;
    int wyk = 0;
    int mxwyk = 0;
    for (int h = 0; h <= MAX_DEG; h++)
    {
        wsp_wielomianu[h] = 0;
    }
    int koniec = dlugosc - 1;//zmienna trzymająca współrzędną końca jednomianu który występuje w linii
    int poczatek = dlugosc - 1;//zmienna szukająca początku jednomianu
    while (poczatek >= 0)
    {
        while (poczatek >= 0 && linia[poczatek] != '+' && linia[poczatek]!='-')
        {
            poczatek--;
        }
        if (koniec>=1 && linia[koniec] >= '1' && linia[koniec] <= '9' && linia[koniec - 1] == '^')//jednomian stopnia
           {
               wyk = linia[koniec]-'0';
               while (koniec >= 0 && linia[koniec] != 'x')koniec--;
               koniec--;
           }
        else if (koniec >=2 && linia[koniec] == '0' && linia[koniec - 2] == '^')//jednomian stopnia dziesiątego
           {
               wyk = 10;
               while (koniec >= 0 && linia[koniec] != 'x')koniec--;
               koniec--;
           }
        else if (linia[koniec] == 'x')//jednomian stopnia pierwszego
        {
            wyk = 1;
            koniec--;
        }
        else
        {
            wyk = 0;
        }
        //pomiędzy zmienną początek a koniec w tablicy linia znajduję się wartość współczynnika
        mxwyk = wyk;
        long long pom = 1;
        wsp = 0;
        while (koniec >= 0 && linia[koniec] >= '0' && linia[koniec] <= '9')// obliczanie wartości współczynnika
        {
            wsp += (linia[koniec]-'0') * (int)pom;
            pom *= 10;
            koniec--;
        }
        if (koniec >= -1 && linia[koniec+1] == 'x')wsp = 1;
        if (koniec >= 0 && linia[koniec] == '-')wsp *= -1;
        poczatek--;
        koniec = poczatek;
        wsp_wielomianu[wyk] = wsp;
    }
    return mxwyk;
}
static struct para wczytywanie_jednej_linii(int linia[], int wsp_wielomianu[], int dzialanie)//funkcja wczytująca jedną linię
{
    int znak;
    int i = 0;
    while ((znak = getchar()) != '\n' && znak != EOF)
    {
        if (znak != ' ')
        linia[i++] = znak;
    }
    int dlugosc = konwersja(linia, wsp_wielomianu, i);
    struct para wielomian;
    wielomian.dzialanie = dzialanie;
    wielomian.dlugosc = dlugosc;
    return wielomian;
}
static void dzialania_na_wielomianach(int linia[], int tablica_wynikowa[])// funkcja która dla kolejnych wierszy aktualizuje wielomian wynikowy i go wypisuje
{
    int dzialanie;
    while ((dzialanie = getchar()) != '.')
    {
        int wsp_wielomianu[MAX_DEG + 1];
        for (int h = 0; h <= MAX_DEG; h++) wsp_wielomianu[h] = 0;
        struct para wielomian = wczytywanie_jednej_linii(linia, wsp_wielomianu, dzialanie);
        for (int h = 0; h <= wielomian.dlugosc; h++)
        {
            wielomian.tablica[h] = wsp_wielomianu[h];
        }
        if (wielomian.dzialanie == '+')
        {
            dodawanie(wielomian.tablica, tablica_wynikowa, wielomian.dlugosc);
        }
        else
        {
            mnozenie(wielomian.tablica, tablica_wynikowa, wielomian.dlugosc);
        }
        wypisywanie_wsp(tablica_wynikowa, MAX_DEG);
    }
}
int main()
{
    int linia[LINE_LENGTH + 1];
    int tablica_wynikowa[MAX_DEG + 1];//"akumulator" początkowy
    for (int h = 0; h <= MAX_DEG; h++) tablica_wynikowa[h] = 0;
    dzialania_na_wielomianach(linia, tablica_wynikowa);
}
