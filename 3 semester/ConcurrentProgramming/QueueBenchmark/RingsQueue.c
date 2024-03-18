#include <malloc.h>
#include <pthread.h>
#include <stdatomic.h>

#include <stdio.h>
#include <stdlib.h>

#include "HazardPointer.h"
#include "RingsQueue.h"

struct RingsQueueNode;
typedef struct RingsQueueNode RingsQueueNode;

struct RingsQueueNode {
    _Atomic(RingsQueueNode*) next;
    _Atomic(size_t) push_idx;
    _Atomic(size_t) pop_idx;
    Value items[RING_SIZE];
};

RingsQueueNode* RingsQueueNode_new(void)
{
    RingsQueueNode* node = (RingsQueueNode*)malloc(sizeof(RingsQueueNode));
    atomic_store(&node->next, NULL);
    atomic_store(&node->push_idx, 0);
    atomic_store(&node->pop_idx, 0);
    for (size_t i = 0; i < RING_SIZE; i++) {
        atomic_store(&node->items[i], EMPTY_VALUE);
    }

    return node;
}

struct RingsQueue {
    RingsQueueNode* head;
    RingsQueueNode* tail;
    pthread_mutex_t pop_mtx;
    pthread_mutex_t push_mtx;
};

RingsQueue* RingsQueue_new(void)
{
    RingsQueue* queue = (RingsQueue*)malloc(sizeof(RingsQueue));
    pthread_mutex_init(&queue->pop_mtx, NULL);
    pthread_mutex_init(&queue->push_mtx, NULL);
    queue->head = queue->tail = RingsQueueNode_new();

    return queue;
}

void RingsQueue_delete(RingsQueue* queue)
{
    pthread_mutex_destroy(&queue->pop_mtx);
    pthread_mutex_destroy(&queue->push_mtx);

    RingsQueueNode* node = queue->head;
    while (node != NULL) {
        RingsQueueNode* next;
        atomic_store(&next, node->next);
        free(node);
        node = next;
    }

    free(queue);
}

void RingsQueue_push(RingsQueue* queue, Value item)
{
    pthread_mutex_lock(&queue->push_mtx);
    RingsQueueNode* tail = queue->tail;
    size_t idx = atomic_load(&tail->push_idx);
    size_t elements = idx - atomic_load(&tail->pop_idx);
    if (elements == RING_SIZE) {
        // Add a new node to the queue.
        RingsQueueNode* new_tail = RingsQueueNode_new();
        atomic_store(&new_tail->items[0], item);
        atomic_store(&new_tail->push_idx, 1);
        atomic_store(&tail->next, new_tail);
        queue->tail = new_tail;
    } else {
        atomic_store(&tail->items[idx % RING_SIZE], item);
        atomic_store(&tail->push_idx, idx + 1);
    }
    pthread_mutex_unlock(&queue->push_mtx);
}

Value RingsQueue_pop(RingsQueue* queue)
{
    pthread_mutex_lock(&queue->pop_mtx);
    RingsQueueNode* head = queue->head;
    size_t idx = atomic_load(&head->pop_idx);
    if (atomic_load(&head->push_idx) == idx) {
        if (queue->tail == head) {
            pthread_mutex_unlock(&queue->pop_mtx);
            return EMPTY_VALUE;
        }
        RingsQueueNode* next = atomic_load(&queue->head->next);
        Value item = atomic_load(&next->items[0]);
        atomic_store(&next->pop_idx, 1);
        queue->head = next;
        pthread_mutex_unlock(&queue->pop_mtx);
        return item;
    } else {
        Value item = atomic_load(&head->items[idx % RING_SIZE]);
        atomic_store(&head->pop_idx, idx + 1);
        pthread_mutex_unlock(&queue->pop_mtx);
        return item;
    }
}

bool RingsQueue_is_empty(RingsQueue* queue)
{
    pthread_mutex_lock(&queue->pop_mtx);
    RingsQueueNode* head = queue->head;
    size_t elements = atomic_load(&head->push_idx) - atomic_load(&head->pop_idx);
    bool empty = elements == 0 && atomic_load(&head->next) == NULL;
    pthread_mutex_unlock(&queue->pop_mtx);
    return empty;
}
