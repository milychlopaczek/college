/** @file
 * Implementacja klasy przechowującej przekierowania numerów telefonicznych
 *
 * @author Jakub Kuzioła <jk438572@mimuw.edu.pl>
 * @copyright Uniwersytet Warszawski
 * @date 2022
 */

#include "phone_forward.h"
#include <stdlib.h>
#include <ctype.h>

/**
 * Ilość cyfr w numerze.
 */
#define NUMOFDIGITS 12

/**
 * @brief Struktura przechowująca przekierowania (trie).
 */
struct PhoneForward
{
    struct PhoneForward* child[NUMOFDIGITS]; ///< Dzieci wierzchołka. 
    char * number; ///< Przekierowanie numeru.
    size_t length; ///< Długość przekierowania.
    struct PhoneForward * parent; ///< Ojciec wierzchołka.
    int lastDigit; ///< ostatnia cyfra.
};


/**
 * @brief Struktura przechowująca numery telefonów.
 * 
 */
struct PhoneNumbers
{
    char ** numbers; ///< Numery telefonów.
    size_t allocated; ///< Ilość zaalokowanych komórek na numery.
    size_t length; ///< Ilość numerów w strukturze.
};

/**
 * @brief 
 * Funkcja tworząca i dodająca nowego syna węzła pf.
 * @param pf - wskaźnik na węzeł.
 * @param path - indeks syna, który ma być dodany.
 * @return PhoneForward* - wskaźnik na dodany węzeł.
 */
static PhoneForward* addNode(PhoneForward* pf, int path)
{
    PhoneForward* new = calloc(1, sizeof(PhoneForward));
    if (new == NULL) //  Jeśli alokacja pamięci się nie udała.
    {
        return NULL;
    }
    for (int i = 0; i < NUMOFDIGITS; i++)
    {
        new->child[i] = NULL;
    }
    new->number = NULL;
    new->length = 0;
    new->parent = pf;
    new->lastDigit = path;
    pf->child[path] = new;
    return new;
}

/**
 * @brief Funkcja konwertująca znak w numerze telefonu na odpowiadającą jemu wartość.
 * @param c - znak do konwersji
 * @return - wartość znaku.
 */
static int charValue(const char c)
{
    if (c == '*')
    {
        return 10;
    }
    else if (c == '#')
    {
        return 11;
    }
    return (int)(c) - 48;
}

/**
 * @brief Funkcja konwertująca wartosc na znak w numerze telefonu.
 * @param c - wartosc do konwersji
 * @return - znak.
 */
static char intValue(const int c)
{
    if (c == 11)
    {
        return '#';
    }
    else if(c == 10)
    {
        return '*';
    }
    return (char)('0' + c);
}

/**
 * @brief
 * Funkcja określająca czy poprawny numer telefonu może zawierać znak 'c'.
 * @param c - znak do sprawdzenia.
 * @return true: Znak jest poprawny.
 * false: Znak nie jest poprawny.
 */
static bool isCharDigit(const char c)
{
    if (isdigit(c) || c == '*' || c == '#')
    {
        return true;
    }
    return false;
}

/**
 * @brief 
 * Funkcja, która sprawdza, czy napis prezentuje
 * poprawny numer telefonu.
 * @param num - napis wejsciowy.
 * @return true - Number jest poprawny.
 * @return false - Number nie jest poprawny.
 */
static bool validNumber(char const * num)
{
    if (num == NULL)
    {
        return false;
    }
    if (*num == '\0')
    {
        return false;
    }
    size_t i = 0;
    while (num[i] != '\0')
    {
        if (!isCharDigit(num[i])) // Znak nie jest cyfrą.
        {
            return false;
        }
        i++;
    }
    if (i == 0) // Napis jest pusty.
    {
        return false;
    }
    return true;
}

/**
 * @brief 
 * Funkcja zwracająca długość napisu.
 * @param string - napis.
 * @return size_t - długość napisu.
 */
static size_t stringLength(char const* string)
{
    size_t i = 0;
    if (string == NULL)
    {
        return 0;
    }
    while (string[i] != '\0')
    {
        i++;
    }
    return i;
}

/**
 * @brief Funkcja, która porównuje dwa napisy równej długości.
 * 
 * @param num1 - pierwszy napis.
 * @param num2  - drugi napis.
 * @return true - napisy są identyczne.
 * @return false - napisy są różne.
 */
static bool compareStrings(char const* num1, char const* num2)
{
    size_t i = 0;
    bool id = true;
    while (num1[i] != '\0' && id)
    {
        if (num1[i] != num2[i]) // znak na i-tej pozycji się nie zgadza.
        {
            id = false;
        }
        i++;
    }
    return id;
}

/**
 * @brief Zwiększa długość dostępnych miejsc na numery w strukturze.
 * 
 * @param pn - struktura przechowująca numery telefonów.
 * @return true - operacja się udała.
 * @return false - operacja się nie udała.
 */
static bool extendPhoneNumbers(PhoneNumbers ** pn)
{
    size_t newSize = ((*pn)->allocated * 2) + 1;
    (*pn)->allocated = newSize;
    (*pn)->numbers = realloc((*pn)->numbers, newSize);
    if ((*pn)->numbers == NULL) // Alokacja pamięci się nie udała.
    {
        free(*pn);
        return false;
    }
    return true;
}

/**
 * @brief Zwiększa długość napisu.
 * 
 * @param tab - wskaźnik na napis.
 * @param size - dotychczasowa długość.
 * @return true - operacja się udała.
 * @return false - operacja się nie udała.
 */
static bool extendString(char ** tab, size_t * size)
{
    size_t newSize = (*size) * 2 + 1;
    *size = newSize;
    *tab = realloc(*tab, newSize);
    if (*tab == NULL) // Alokacja pamięci się nie udała.
    {
        return false;
    }
    return true;
}

/**
 * @brief Funkcja kopiująca zawartość napisu do nowego napisu
 * 
 * @param num - napis do skopiowania
 * @return char* - skopiowany napis.
 */
static char * stringCopy(const char * num)
{
    size_t length = stringLength(num);
    // Alokowanie miejsca na nowy napis.
    char * new = calloc(length + 1, sizeof(char));
    if (new == NULL) // Alokacja się nie powiodła.
    {
        return NULL;
    }
    // Kopiowanie napisu.
    int i = 0;
    while (num[i] != '\0')
    {
        new[i] = num[i];
        i++;
    }
    new[i] = '\0';
    return new;
}

/**
 * @brief Funkcja dodająca numer do struktury.
 * 
 * @param pn - strucktura przechowujaca numery telefonów.
 * @param num - napis, który ma być dodany.
 * @return true - napis został dodany.
 * @return false - napis nie został dodany.
 */
static bool addNumber(PhoneNumbers ** pn, const char* num)
{
    //printf("new number in struct\n");
    if ((*pn)->length == (*pn)->allocated)
    {
        if (extendPhoneNumbers(pn) == false) // Wydłużenie struktury się nie powiodło.
        {
            return false;
        }
    }
    // Dodawanie Nowego numeru do struktury.
    if (num == NULL)
    {
        (*pn)->numbers[(*pn)->length] = NULL;
        (*pn)->length += 1;
        return true;
    }
    char * new = stringCopy(num);
    //printf("%s \n", new);
    if (new == NULL)
    {
        return false;
    }
    (*pn)->numbers[(*pn)->length] = new;
    (*pn)->length += 1;
    return true;
}

/**
 * @brief Tworzy nową strukturę przechowująca przekierowania.
 * 
 * @return PhoneForward* - wskaźnik na nową strukturę.
 */
PhoneForward* phfwdNew(void)
{
    PhoneForward* new = calloc(1, sizeof(PhoneForward));
    if (new == NULL) // Alokacja pamięci się nie powiodła.
    {
        return NULL;
    }
    // Ustawianie domyślnych wartości nowej struktury.
    for (int i = 0; i < NUMOFDIGITS; i++)
    {
        new->child[i] = NULL;
    }
    new->number = NULL;
    new->number = 0;
    new->parent = NULL;
    new->lastDigit = -1;
    return new;
}

void phfwdDelete(PhoneForward * pf)
{
    if (pf != NULL)
    {
        for (int i = 0; i < NUMOFDIGITS; i++) // Usuwanie dzieci węzła.
        {
            phfwdDelete(pf->child[i]);
        }
        // Usuwanie węzła.
        free(pf->number);
        free(pf);
    }
}

void phnumDelete(PhoneNumbers *pnum)
{
    if (pnum == NULL)
    {
        return;
    }
    for (size_t i = 0; i < pnum->length; i++) // Usuwanie wszystkich numerów w strukturze.
    {
        free((pnum->numbers)[i]);
        (pnum->numbers)[i] = NULL;
    }
    // Usuwanie samej struktory.
    if (pnum->numbers != NULL)
    {
        free(pnum->numbers);
    }
    free(pnum);
    pnum = NULL;
}

/**
 * @brief Funkcja inicjalizująca nową strukturę przechowującą
 * numery telefonów.
 * @return PhoneNumbers* - wskaźnik na wynikową strukturę.
 */
static PhoneNumbers * phnumInit()
{
    PhoneNumbers * new;
    new = calloc(1, sizeof(PhoneNumbers));
    if (new == NULL) // Alokacja pamięci się nie powiodła.
    {
        return NULL;
    }
    new->length = 0;
    new->numbers = calloc(100, sizeof(char*));
    new->allocated = 100;
    if (new->numbers == NULL) // Alokacja pamięci się nie powiodła.
    {
        free(new);
        return NULL;
    }
    return new;
}

PhoneNumbers * phfwdGet(PhoneForward const *pf, char const *num)
{
    // Sprawdzanie czy dane wejsciowe są poprawne.
    if (pf == NULL)
    {
        return NULL;
    }
    if (!validNumber(num))
    {
        PhoneNumbers * resultNumber = phnumInit();
        addNumber(&resultNumber, NULL);
        return resultNumber;
    }
    size_t length = stringLength(num);
    if (length == 0) // napis jest pusty.
    {
        char * result;
        result = stringCopy(num);
        PhoneNumbers * resultNumber = phnumInit();
        addNumber(&resultNumber, result);
        free(result);
        return resultNumber;
    }
    // Wyznaczanie przekierowania najdłuższego prefiksu numeru.
    char * forwardPrefix = NULL;
    size_t prefixLength = 0;
    size_t i = 0;
    while (i < length && pf != NULL)
    {
        int child = charValue(num[i]);
        pf = pf->child[child];
        if (pf != NULL && pf->number != NULL) // Znaleziono kandydata na przekierowanie.
        {
            forwardPrefix = pf->number;
            prefixLength = i + 1;
        }
        i++;
    }
    // Inicjalizacja wynikowej struktury.
    PhoneNumbers * resultNumber = phnumInit();
    char * result;
    if (forwardPrefix == NULL) // Nie znaleziono przekierowania.
    {
        result = stringCopy(num);
    }
    else // Znaleziono przekierowanie.
    {
        // Wyznaczanie nowego numeru.
        size_t forwardPrefixLength = stringLength(forwardPrefix);
        size_t resultLength = length - prefixLength + forwardPrefixLength;
        result = calloc(resultLength + 1, sizeof(char));
        if (result == NULL)
        {
            phnumDelete(resultNumber);
            return NULL;
        }
        for (i = 0; i < forwardPrefixLength; i++)
        {
            result[i] = forwardPrefix[i];
        }
        for (i = 0; i < length - prefixLength; i++)
        {
            result[i + forwardPrefixLength] = num[i + prefixLength];
        }
        result[resultLength] = '\0';
    }
    // Dodanie nowego numeru do struktury.
    addNumber(&resultNumber, result);
    free(result);
    return resultNumber;
}

bool phfwdAdd(PhoneForward* pf, char const * num1, char const * num2)
{
    // Sprawdzanie, czy napisy są poprawne.
    if (pf == NULL)
    {
        return false;
    }
    if (!validNumber(num1) || !validNumber(num2))
    {
        return false;
    }
    size_t i = 0;
    PhoneForward* prev = pf;
    size_t length2 = stringLength(num2);
    size_t length1 = stringLength(num1);
    if (length1 == length2)
    {
        if (compareStrings(num1, num2))
        {
            return false;
        }
    }
    // Iteracja po drzewie na ścieżce: num1.
    while (pf != NULL && i < length1) // Iterowanie się po istniejących węzłąch drzewa.
    {
        prev = pf;
        int child = charValue(num1[i]);
        pf = pf->child[child];
        i++;
    }
    pf = prev;
    for (size_t j = i - 1; j < length1; j++) // Alokowanie nowych węzłów w drzewie.
    {
        int child = charValue(num1[j]);
        if (pf->child[child] == NULL)
        {
            // Tworzenie nowego węzła.
            pf = addNode(pf, child);
        }
        else
        {
            pf = pf->child[child];
        }
        if (pf == NULL)
        {
            return false;
        }
    }
    // Modyfikacja docelowego przekierowania w węźle.
    free(pf->number);
    char * number = stringCopy(num2);
    pf->number = number;
    pf->length = length2;
    return true;
}

char const * phnumGet(PhoneNumbers const *pnum, size_t id)
{
    if (pnum == NULL || id >= pnum->length)
    {
        return NULL;
    }
    // Zwracanie numeru o indeksie id.
    return pnum->numbers[id];
}

/**
 * @brief Funkcja szukająca węzła w drzewie po ścieżce z napisu.
 * 
 * @param pf - korzeń struktury.
 * @param num - napis.
 * @param length - długość napisu.
 * @return PhoneForward* - szukany napis.
 */
static PhoneForward * findNode(PhoneForward *pf, char const *num, size_t length)
{
    size_t i = 0; // i - głębokość drzewa.
    // Iteracja po węzłach w drzewie.
    while (i < length && pf != NULL)
    {
        int child = charValue(num[i]);
        pf = pf->child[child];
        i++;
    }
    return pf;
}


/**
 * @brief Funkcja usuwająca całe poddrzewo węzła.
 * 
 * @param pf - węzeł wejściowy.
 */
static void deleteSubTrie(PhoneForward ** pf)
{
    if (*pf != NULL)
    {
        for (int i = 0; i < NUMOFDIGITS; i++) // Usuwanie synów węzła.
        {
            deleteSubTrie(&(*pf)->child[i]);
        }
        // Usuwanie węzła.
        free((*pf)->number);
        (*pf)->number = NULL;
        free(*pf);
        *pf = NULL;
    }
}

/**
 * @brief Funkcja zwalniająca węzły na ścieżce, jeśli nie mają żadnych synów 
 * oraz własnej wartości.
 * 
 * @param pf - korzeń drzewa.
 * @param num - napis.
 * @param length - długość napisu.
 * @param i - głębokość w drzewie.
 * @return true - Węzeł został usunięty.
 * @return false - Węzeł nie został usunięty.
 */
static void clearPath(PhoneForward ** pf, char const * num, size_t length, size_t i)
{
    if (*pf != NULL && i < length)
    {
        int childPath = charValue(num[i]);
        if (length == i + 1) // Doszliśmy do końca ścieżki.
        {
            (*pf)->child[childPath] = NULL;
        }
        clearPath(&(*pf)->child[childPath], num, length, i + 1);
    }
}

/**
 * @brief Sprawdzanie czy @p pref jest prefiksem @p num.
 * 
 * @param num - napis wejsciowy.
 * @param pref - prefiks do sprawdzenia.
 * @param numLength - długość numeru.
 * @param prefLength  - długość prefiksu.
 * @return true - jest prefiksem.
 * @return false - nie jest prefiksem.
 */
static bool isPrefix(char const *num, char const *pref, size_t numLength, size_t prefLength)
{
    if (numLength < prefLength)
    {
        return false;
    }
    for (size_t i = 0; i < prefLength; i++)
    {
        if (num[i] != pref[i])
        {
            return false;
        }
    }
    return true;
}

/**
 * @brief Funckja zamieniająca prefiks napisu.
 * 
 * @param num - napis
 * @param newPref - nowy prefiks
 * @param numLength - długość numeru.
 * @param prefLength - długość prefiksu do zamiany.
 * @param newPrefLength - długość nowego prefiksu
 * @return char*  - wynikowy napis.
 */
static char * replacePrefix(char const * num, char const * newPref, size_t numLength, size_t prefLength, size_t newPrefLength)
{
    char * result;
    result = calloc(numLength + newPrefLength - prefLength + 1, sizeof(char));
    for (size_t i = 0; i < newPrefLength; i++)
    {
        result[i] = newPref[i];
    }
    for (size_t i = newPrefLength; i < numLength - prefLength + newPrefLength; i++)
    {
        result[i] = num[i - newPrefLength + prefLength];
    }
    result[numLength - prefLength + newPrefLength] = '\0';
    return result;
}

/**
 * @brief Funkcja znajdująca numery pasujące do opisu funkcji phfwdReverse.
 * @param pf - struktura przechowująca przekierowania.
 * @param num - numer.
 * @param length - długość numeru.
 * @param pnum - struktura przechowująca wynikowe numery telefonów.
 */
static void findValue(PhoneForward const *pf, char const *num, const size_t length, PhoneNumbers ** pnum)
{
    if (pf == NULL)
    {
        return;
    }
    size_t depth = 0;
    size_t allocated = 100;
    char * number = calloc(allocated, sizeof(char));
    int i = 0;
    // number - 
    // Używam algorytmu dfs bez dodatkowej pamięci (bez liniowej pamięci).
    while (pf != NULL)
    {
        bool found = false;
        // Przechodzenie po synach wierzchołka.
        for (int j = i; j < NUMOFDIGITS; j++)
        {
            if (pf->child[j] != NULL)
            {
                i = 0;
                pf = pf->child[j];
                found = true;
                if (allocated == depth + 1)
                {
                    if (extendString(&number, &allocated) == false) // Jeżeli realloc się nie powiódł.
                    {
                        free(number);
                        return;
                    }
                }
                number[depth] = intValue(pf->lastDigit);
                depth++;
                break;
            }
        }
        // Jeżeli nie znaleźliśmy żadnego syna w którym nie byliśmy.
        if (!found)
        {
            if (pf->lastDigit >= 0)
            {
                i = pf->lastDigit + 1;
                // Sprawdzam czy numer pasuje
                if (pf->number != NULL && isPrefix(num, pf->number, length, pf->length)) {
                    char * newNum = replacePrefix(num, number, length, pf->length, depth);
                    if (newNum != NULL)
                    {
                        addNumber(pnum, newNum);
                        free(newNum);
                    }
                }
            }
            depth--;
            // Przeszliśmy całe poddrzewo wierzchołka, więc wracamy do ojca.
            pf = pf->parent;
        }
    }
    free(number);
}

/**
 * @brief Porównuje napisy.
 * @param num1
 * @param num2
 * @return 1 - num1 > num2.
 * -1 - num2 > num1
 * 0 - num1 = num2.
 */
static int greaterNum(char const * num1, char const * num2)
{

        if (num1 == NULL && num2 == NULL)
        {
            return 0;
        }
        if (num1 == NULL)
        {
            return 1;
        }
        if (num2 == NULL)
        {
            return -1;
        }
        size_t i = 0;
        // Porównywanie niepustych napisów.
        while (num1[i] != '\0' && num2[i] != '\0') 
        {
            if(charValue(num1[i]) > charValue(num2[i]))
            {
                return 1;
            }
            else if(charValue(num1[i]) < charValue(num2[i]))
            {
                return -1;
            }
            i++;
        }
        // num1 jest prefiksem num2.
        if (num1[i] == '\0' && num2[i] != '\0')
        {
            return -1;
        }
        // num2 jest prefiksem num1.
        if (num1[i] != '\0' && num2[i] == '\0')
        {
            return 1;
        }
        return 0;
}

/**
 * @brief Funkcja porównująca dwa obiekty używana przez funckję qsort.
 * 
 * @param s1 - pierwszy obiekt
 * @param s2  - drugi obiekt
 * @return -1 => s1 > s2
 * 1 => s1 < s2
 * 0 => s1 = s2
 */
static int phoneNumbersCompare(void const * s1, void const * s2)
{
    // Sprawdzanie czy jakiś obiekt jest wskaźnikiem na NULL.
    if (s1 == NULL && s2 == NULL)
    {
        return 0;
    }
    else if (s1 == NULL)
    {
        return 1;
    }
    else if (s2 == NULL)
    {
        return -1;
    }
    char ** z1 = (char **)s1;
    char ** z2 = (char **)s2;
    return greaterNum(*z1, *z2);
}

PhoneNumbers * phfwdReverse(PhoneForward const *pf, char const *num)
{
    // Wyłapywanie niepoprawnych danych.
    if (pf == NULL)
    {
        return NULL;
    }
    if (num == NULL || !validNumber(num))
    {
        PhoneNumbers * resultNumber = phnumInit();
        addNumber(&resultNumber, NULL);
        return resultNumber;
    }
    size_t length = stringLength(num);
    PhoneNumbers * result = phnumInit();
    addNumber(&result, num);
    // Funkcja do szukania pasujących numerów.
    findValue(pf, num, length, &result);
    // Sortowanie numerów.
    qsort(result->numbers, result->length, sizeof(char *), phoneNumbersCompare);
    // Usuwanie duplikatów.
    PhoneNumbers * resultFinal = phnumInit();
    size_t lastLength = 0;
    for (size_t j = 0; j < result->length; j++)
    {
        bool duplicate = false;
        if (j > 0 && lastLength == stringLength(phnumGet(result, j)))
        {
            if (compareStrings(phnumGet(result, j), phnumGet(result, j - 1)) == true)
            {
                duplicate = true;
            }
        }
        if (duplicate == false)
        {
            addNumber(&resultFinal, phnumGet(result, j));
            lastLength = stringLength(phnumGet(result, j));
        }
    }
    phnumDelete(result);
    return resultFinal;
}

void phfwdRemove(PhoneForward *pf, char const *num)
{
    // Sprawdzanie poprawności napisu i struktury.
    if (pf == NULL || !validNumber(num))
    {
        return;
    }
    size_t length = stringLength(num);
    // Szukanie węzła, którego poddrzewo trzeba usunąć.
    PhoneForward * subTrie = findNode(pf, num, length);
    if (subTrie != NULL)
    {
        // Usuwanie poddrzewa.
        deleteSubTrie(&subTrie);
        clearPath(&pf, num, length, 0);
    }
}
PhoneNumbers * phfwdGetReverse(PhoneForward const *pf, char const *num)
{
    // Wynik funkcji Reverse jest nadzbiorem wyniku funkcji GetReverse.
    PhoneNumbers * rev = phfwdReverse(pf, num);
    // Corner case'y.
    if (rev == NULL)
    {
        return NULL;
    }
    else if (phnumGet(rev, 0) == NULL)
    {
        return rev;
    }
    size_t numLength = stringLength(num);
    PhoneNumbers * result = phnumInit();
    for (size_t i = 0; i < rev->length; i++)
    {
        PhoneNumbers * aux = phfwdGet(pf, phnumGet(rev, i));
        const char * auxN = phnumGet(aux, 0);
        if (auxN != NULL)
        {
            size_t l = stringLength(auxN);
            // Sprawdzanie czy numer przechodzi na @param[in] num.
            if (l == numLength && compareStrings(auxN, num))
            {
                // Numer przechodzi na num.
                if (addNumber(&result, phnumGet(rev, i)) == false)
                {
                    return NULL;
                }
            }
            phnumDelete(aux);
        }
    }
    phnumDelete(rev);
    return result;
}
