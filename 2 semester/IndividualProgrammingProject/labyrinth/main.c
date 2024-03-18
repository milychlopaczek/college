#include <stdio.h>
#include <stdbool.h>
#include <ctype.h>
#include <limits.h>
#include <assert.h>
#include <stdlib.h>
#include <stdint.h>
#include "queue.h"

#ifndef BASE_SIZE
#define BASE_SIZE 1000
#endif

const size_t WMAX = 4294967296;

struct dynamicArray
{
    size_t* array;
    size_t length;
    size_t allocated;
};
typedef struct dynamicArray dynamic;

struct input
{
    dynamic* n;
    bool isNReleased;
    bool isXReleased;
    bool isYReleased;
    size_t start;
    size_t end;
};
typedef struct input input;

 // Returns length of reallocated array.
size_t more(size_t n)
{
    return (n / 2 + 1) * 3;
}

 // Reallocates array.
static bool extendArray(size_t **t, size_t *size)
{
    size_t* aux = *t;
    if (*size > (SIZE_MAX / 3 - 1) * 2)
    {
        return true;
    }
    *size = more(*size);
    aux = realloc(aux,  (*size) * sizeof(size_t));
    *t = aux;
    return false;
}

// Reading irrelevant characters.
void readWhiteSpaces()
{
    char character = ' ';
    while (isspace(character) != 0) // Character is a whitespace.
    {
        character = getchar();
    }
    ungetc(character, stdin);
}

// Checks if bit is available in the maze.
bool availableBit(size_t cords, unsigned char* maze, size_t size)
{
    if ((maze[size - cords / 8 - 1] & (1 << (cords % 8))) != 0) // Checking the bite's value in the bitset.
    {
        return false;
    }
    else
    {
        return true;
    }
}

size_t readNumber(size_t limit, int* error) // Reads a number from input.
{
    unsigned int number = 0;
    char character;
    readWhiteSpaces();
    while (isdigit(character = getchar()) != 0)
    {
        size_t decimal = (int)character - '0';
        if ((limit - decimal) / 10 < number)  // Number would be greater than the limit.
            {
                *error = 0;
            }
            else    // Number will still smaller than the limit.
            {
                number = number * 10 + decimal;
            }
    }
    if (isspace(character) == 0 && character != '\n')   //  Unexpected character.
    {
        *error = 4;
    }
    return number;
}

// Adds value to the array.
static bool addToArray(size_t** array, size_t* length, size_t* allocated, size_t val)
{
    size_t* aux = *array;
    bool err = false;
    if ((*length) + 1 >= (*allocated))
    {
        // The array is too small.
        err = extendArray(array, allocated);
    }
    *(aux + (*length)) = val;
    *length += 1;
    *array = aux;
    return err;
}

static size_t mazeSize(dynamic* DArray, int* error) // Calculating the maze size.
{
    size_t size = 1;
    for (size_t i = 0; i < DArray->length && *error == -1; i++)
    {
        if (size > SIZE_MAX / DArray->array[i])
        {
            *error = 0;
        }
        else
        {
            size = size * DArray->array[i];
        }
    }
    return size;
}

 // Reads a line and puts values in the array.
static void readLine(size_t** array, size_t* length, size_t* allocated, int lineNumber, int* error)
{
    char character;
    bool prevnumber = false;
    size_t number = 0;
    while ((character = getchar()) != '\n')
    {
        if (isspace(character) != 0)    //Character is a whitespace.
        {
            if (prevnumber == true)
            {
                if (number != 0)
                {
                    bool err = addToArray(array, length, allocated, number); // Adding the number to array.
                    if (err == true) 
                    {
                        *error = lineNumber;
                    }
                    prevnumber = false;
                    number = 0;
                }
                else
                {
                    *error = lineNumber;
                }
            }
        }
        else if (isdigit(character) != 0)  //   Character is a digit.
        {  
            size_t decimal = (int)character - '0';
            if ((SIZE_MAX - decimal) / 10 < number)
            {
                // Error: number would be greater than size_max.
                *error = lineNumber;
            }
            else    // Number will still smaller than size_max.
            {
                number = number * 10 + decimal;
                prevnumber = true;
            }
        }
        else
        {
            *error = lineNumber;
        }
    }
    if (prevnumber == true) // Adding the last number.
    {
        if (number == 0)
        {
            *error = lineNumber;
        }
        bool err = addToArray(array, length, allocated, number);
        if (err == true)
        {
            *error = lineNumber;
        }
    }
}

 // Initialization of struct dynamic.
bool initalizeDynamic(dynamic** DArray, size_t firstAllocation)
{
    bool err = false;
    *DArray = calloc(1, sizeof(dynamic));
    if (*DArray == NULL)
    {
        err = true;
    }
    (*DArray)->array = calloc(firstAllocation, sizeof(int));
    if ((*DArray)->array == NULL)
    {
        err = true;
    }
    (*DArray)->allocated = firstAllocation;
    (*DArray)->length = 0;
    return err;
}

// Freeing struct dynamic.
static void releaseDynamic(dynamic* DArray)
{
    free(DArray->array);
    free(DArray);
}

// Moves all positive values in the array to the end of it.
void shiftArray(unsigned char* array, size_t size, size_t lastIndex)
{
    for (size_t i = 0; i <= lastIndex; i++)
    {
        array[size - 1 - i] = array[lastIndex - i];
    }
    for (size_t i = lastIndex + 1; i < size; i++)
    {
        array[size - 1 - i] = 0;
    }
}

// Corrects bitset if it is incorrect.
void Transform(unsigned char* array, size_t size)
{
    size_t prev = 0;
    for (size_t i = 0; i <= size; i++)
    {
        size_t value = array[i];
        array[i] = value / 16 + prev * 16;
        prev = value % 16;
    }
}

// Reads 4th line (the first version).
void fourthLine0x(unsigned char* maze, size_t hexSize, int* error, size_t arraySize)
{
    int character;
    size_t i = 0;
    bool first = false;
    while (isalnum(character = getchar()) != 0 && *error == -1)
    {
        if (character != '0' || first != false)
        {
            first = true;
            size_t value = 0;
            if (isalpha(character) != 0)
            {
                value = (size_t)tolower(character) - 'a' + 10;
            }
            else
            {
                value = (size_t)character - '0';
            }
            if (i % 2 == 0)
            {
                value *= 16;
            }
            if (i >= hexSize)
            {
                *error = 4;
            }
            else
            {
                maze[i / 2] += value;
            }
            i++;
        }
    }
    if (i % 2 != 0)
    {
        Transform(maze, i/2);
    }
    if (first == true)
    {
        shiftArray(maze, arraySize, (i - 1) / 2);
    }
    ungetc(character, stdin);
}

// Returns number^pow.
static size_t power(long long number, size_t pow)
{
    size_t result = 1;
    for (size_t i = 0; i < pow; i++)
    {
        result *= number;
    }
    return result;
}

// Makes bite nr. number unavailable in the maze.
static void writeToArray(unsigned char* maze, size_t number, size_t arraySize)
{
    size_t byteIndex = number / 8;
    if (availableBit(number, maze, arraySize) == true)
    {
        maze[arraySize - byteIndex - 1] += power(2, (number % 8));
    }
}

// Reads 4th line (the second version).
static void fourthLineR(unsigned char* maze, size_t size, size_t arraySize, int* error)
{
    size_t a = readNumber(UINT_MAX, error);
    size_t b = readNumber(UINT_MAX, error);
    size_t m = readNumber(UINT_MAX, error);
    size_t r = readNumber(UINT_MAX, error);
    size_t s = readNumber(UINT_MAX, error);
    size_t prev;
    for (size_t i = 0; i <= r; i++)
    {
        prev = s;
        s = s % size;
        s = s % WMAX;
        for (size_t j = 0; s + j * WMAX < size; j++)
        {
            writeToArray(maze, s + j * WMAX, arraySize);
        }
        s = (a * prev + b) % m;
    }
}

// Initiates an array (sets values to 0).
void initArray(unsigned char* array, size_t size)
{
    for (size_t i = 0; i < size; i++)
    {
        array[i] = 0;
    }
} 

size_t mazeBit(size_t* coordinates, size_t numberOfDimensions, size_t* dimensions, int* error, int linenumber)
{
    size_t result = 0;
    size_t dimensionsQuotient = 1;
    for (size_t i = 0; i < numberOfDimensions && *error == -1; i++)
    {
        if (coordinates[i] == 0)
        {
            *error = linenumber;
        }
        else
        {
            result += (coordinates[i] - 1) * dimensionsQuotient;
            dimensionsQuotient *= dimensions[i];
        }
    }
    return result;
}

unsigned char* fourthLine(size_t size, int* error)
{
    readWhiteSpaces();
    size_t arraySize = size/8 + (size % 8 != 0);
    size_t hexSize = size/4 + (size % 4 != 0);
    unsigned char* maze = calloc(arraySize + 2, sizeof(unsigned char));
    if (maze == NULL)
    {
        *error = 0;
        return maze;
    }
    else
    {
        initArray(maze, arraySize);
        int character = getchar();
        if (character == 'R')
        {
            readWhiteSpaces();
            fourthLineR(maze, size, arraySize, error); 
        }
        else if (character == '0' && getchar() == 'x')
        {
            fourthLine0x(maze, hexSize, error, arraySize);
        }
        else
        {
            *error = 4;
            return maze;
        }
        readWhiteSpaces();
        if ((character = getchar()) == '\n' && getchar() != EOF)
        {
            *error = 5;
        }
        else if (character != EOF)
        {
            *error = 4;
        }
        return maze;
    }
}

void bfs(unsigned char* maze, input* labyrinth, int* error, size_t arraySize, size_t size)
{
    size_t* dimensions = labyrinth->n->array;
    size_t start = labyrinth->start;
    size_t end = labyrinth->end;
    size_t numberOfDimensions = labyrinth->n->length;
    size_t result = 0;
    queue_t *q = malloc(sizeOfQueue());
    bool ifend = false;
    init(q);
    push(&q, start + 1);
    push(&q, 0);
    while (empty(q) == false && ifend == false)
    {
        if(top(q) == 0)
        {
            result++;
            pop(&q);
            if (empty(q) == false)
            {
                push(&q, 0);
            }
        }
        else if (availableBit(top(q) - 1, maze, arraySize) == false)
        {
            pop(&q);
        }
        else
        {
            size_t bit = top(q) - 1;
            writeToArray(maze, bit, arraySize);
            if (bit == end)
            {
                ifend = true;
            }
            size_t dimensionsQuotient = 1;
            for (size_t i = 0; i < numberOfDimensions && ifend == false; i++)   //  Iterating through possible neighbours of "bit" in the maze.
            {
                    if ((bit + dimensionsQuotient) < size && (bit / (dimensions[i] * dimensionsQuotient)) == ((bit + dimensionsQuotient) / (dimensions[i] * dimensionsQuotient))
                    && availableBit(bit + dimensionsQuotient, maze, arraySize) == true)
                    {
                        push(&q, bit + 1 + dimensionsQuotient);
                    }
                    if (bit >= dimensionsQuotient && (bit / (dimensions[i] * dimensionsQuotient)) == ((bit - dimensionsQuotient) / (dimensions[i] * dimensionsQuotient))
                    && availableBit(bit - dimensionsQuotient, maze, arraySize) == true)
                    {
                        push(&q, bit + 1 - dimensionsQuotient);
                    }
                    dimensionsQuotient *= dimensions[i];
            }
            pop(&q);
        }
    }
    if (*error != -1)
    {
        *error = 0;
        clear(&q);
        free(q);
    }
    else if (ifend == false)
    {
        printf("NO WAY\n");
        clear(&q);
        free(q);
    }
    else
    {
        clear(&q);
        free(q);
        printf("%lu\n", result);
    }
}

static int read(input** labyrinth)
{
    int error = -1;
    bool err = false;
    bool isXReleased = false;
    bool isYReleased = false;
    dynamic* n;
    dynamic* x;
    dynamic* y;
    err = initalizeDynamic(&n, BASE_SIZE);
    readLine(&(n->array), &(n->length), &(n->allocated), 1, &error);
    if (err == true)
    {
        error = 0;
        (*labyrinth)->isNReleased = true;
    }
    else
    {
        (*labyrinth)->isNReleased = false;
    }

    if (error == -1)
    {
        err = initalizeDynamic(&x, n->allocated);
        readLine(&(x->array), &(x->length), &(x->allocated), 2, &error);
        if (err == true)
        {
            error = 0;
        }
        else if (x->length != n->length)
        {
            error = 2;
            if (isXReleased == false)
            {
                releaseDynamic(x);
                isXReleased = true;
            }
            if ((*labyrinth)->isNReleased == false)
            {
                releaseDynamic(n);
                (*labyrinth)->isNReleased = true;
            }
        }
        else
        {
            isXReleased = false;
        }
    }
    if (error == -1)
    {
        err = initalizeDynamic(&y, n->allocated);
        readLine(&(y->array), &(y->length), &(y->allocated), 3, &error);
        if (err == true)
        {
            isYReleased = true;
            error = 0;
        }
        if (y->length != n->length)
        {
            error = 3;
            if (isXReleased == false)
            {
                releaseDynamic(x);
                isXReleased = true;
            }
            if (isYReleased == false)
            {
                releaseDynamic(y);
                isYReleased = true;
            }
            if ((*labyrinth)->isNReleased == false)
            {
                releaseDynamic(n);
                (*labyrinth)->isNReleased = true;
            }
        }
    }
    if (error == -1)
    {
        (*labyrinth)->n = n;
        (*labyrinth)->start = mazeBit(x->array, n->length, n->array, &error, 2);
        (*labyrinth)->end = mazeBit(y->array, n->length, n->array, &error, 3);
        (*labyrinth)->isXReleased = isXReleased;
        (*labyrinth)->isYReleased = isYReleased;
        if (isXReleased == false)
        {
            releaseDynamic(x);
            isXReleased = true;
        }
        if (isYReleased == false)
        {
            releaseDynamic(y);
            isYReleased = true;
        }
    }
    if (isXReleased == false)
    {
        releaseDynamic(x);
        isXReleased = true;
    }
    if (isYReleased == false)
    {
        releaseDynamic(y);
        isYReleased = true;
    }
    return error;
}

int main()
{
    input* labyrinth = calloc(1, sizeof(input));
    int error = read(&labyrinth);
    if (error == -1)
    {
        size_t size = mazeSize(labyrinth->n, &error);
        unsigned char* maze = fourthLine(size, &error);
        if (error == -1)
        {
            size_t arraySize = size/8 + (size % 8 != 0);
            if (availableBit(labyrinth->start, maze, arraySize) == false)
            {
                error = 2;
            }
            else if (availableBit(labyrinth->end, maze,arraySize) == false)
            {
                error = 3;
            }
            if (error == -1)
            {
                bfs(maze, labyrinth, &error, arraySize, size);
            }
            free(maze);
        }
    }
    if (labyrinth->isNReleased == false)
    releaseDynamic(labyrinth->n);

    free(labyrinth);
    if (error != -1)
    {
        fprintf(stderr, "ERROR %d\n",error);
    }
    return 0;
}
