#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include "queue.h"

struct list
{
    size_t val;
    list_t* next;
};

// Adds val to the list assuming that list != NULL.
void add(list_t** l, size_t val)
{
    if (*l == NULL)
    {
        list_t* new = calloc(1, sizeof(list_t));
        new->val = val;
        new->next = NULL;
        *l = new;
    }
    else
    {
        list_t* next = (*l)->next;
        list_t* new = calloc(1, sizeof(list_t));
        new->val = val;
        new->next = next;
        (*l)->next = new;
    }
}

struct queue
{
    list_t* first;
    list_t* last;
};

// True if queue is empty otherwise false.
bool empty(queue_t* q)
{
    return (q->first == NULL);
}

// Initiates queue object.
void init(queue_t* q)
{
    q->first = NULL;
}

// Removes first value from the queue.
void pop(queue_t** q)
{
    list_t* aux = (*q)->first;
    if ((*q)->first == (*q)->last)
    {
        (*q)->first = NULL;
    }
    else
    {
        (*q)->first = (*q)->first->next;
    }
    free(aux);
}

// Returns value of the queue top.
size_t top(queue_t* q)
{
    return q->first->val;
}

// Adds val to the queue.
void push(queue_t** q, size_t val)
{
    if ((*q)->first == NULL)
    {
        (*q)->first = calloc(1, sizeof(list_t));
        (*q)->first->val = val;
        (*q)->last = (*q)->first;
    }
    else
    {
        add(&((*q)->last), val);
        (*q)->last = (*q)->last->next;
    }
}


// Clears the queue.
void clear(queue_t** q)
{
    while ((*q)->first != NULL)
    {
        pop(q);
    }
}

size_t sizeOfQueue()
{
    return sizeof(queue_t);
}