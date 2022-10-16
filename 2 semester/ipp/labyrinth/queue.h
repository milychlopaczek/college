#ifndef _QUEUE_H
#define _QUEUE_H

//Struct of a queue.
typedef struct queue queue_t;

//  Struct of list.
typedef struct list list_t;

void add(list_t** l, size_t val); //  Add value to the list.
bool empty(queue_t* q); // True if queue is empty otherwise false.
void init(queue_t* q); // Initiates queue object.
void pop(queue_t** q); // Removes first value from the queue.
size_t top(queue_t* q); // Returns value of the queue top.
void push(queue_t** q, size_t val); // Adds val to the queue.
bool full(queue_t* q); // Checks if queue is full.
void clear(queue_t** q); // Clears the queue.
size_t sizeOfQueue(); // size of Queue.

#endif