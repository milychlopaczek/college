#include <malloc.h>
#include <pthread.h>
#include <stdatomic.h>

#include "SimpleQueue.h"

struct SimpleQueueNode;
typedef struct SimpleQueueNode SimpleQueueNode;

struct SimpleQueueNode {
    _Atomic(SimpleQueueNode*) next;
    Value item;
};

SimpleQueueNode* SimpleQueueNode_new(Value item)
{
    SimpleQueueNode* node = (SimpleQueueNode*)malloc(sizeof(SimpleQueueNode));
    if(node == NULL) {
        return NULL;
    }
    atomic_init(&node->next, NULL);
    atomic_init(&node->item, item);

    return node;
}

struct SimpleQueue {
    SimpleQueueNode* head;
    SimpleQueueNode* tail;
    pthread_mutex_t head_mtx;
    pthread_mutex_t tail_mtx;
};

SimpleQueue* SimpleQueue_new(void)
{
    SimpleQueue* queue = (SimpleQueue*)malloc(sizeof(SimpleQueue));
    if(queue == NULL) {
        return NULL;
    }
    queue->head = SimpleQueueNode_new(EMPTY_VALUE); // Create a dummy node.
    queue->tail = queue->head;
    pthread_mutex_init(&queue->head_mtx, NULL);
    pthread_mutex_init(&queue->tail_mtx, NULL);
    return queue;
}

void SimpleQueue_delete(SimpleQueue* queue)
{
    SimpleQueueNode* node = queue->head;
    while (node != NULL) {
        SimpleQueueNode* next = atomic_load(&node->next);
        free(node);
        node = next;
    }

    pthread_mutex_destroy(&queue->head_mtx);
    pthread_mutex_destroy(&queue->tail_mtx);
    free(queue);
}

void SimpleQueue_push(SimpleQueue* queue, Value item)
{
    SimpleQueueNode* node = SimpleQueueNode_new(item);
    pthread_mutex_lock(&queue->tail_mtx);
    SimpleQueueNode* tail = atomic_load(&queue->tail);
    atomic_store(&tail->next, node);
    queue->tail = node;
    pthread_mutex_unlock(&queue->tail_mtx);
}

Value SimpleQueue_pop(SimpleQueue* queue)
{
    pthread_mutex_lock(&queue->head_mtx);
    SimpleQueueNode* head = queue->head;
    SimpleQueueNode* node = atomic_load(&head->next);
    if (node == NULL) {
        pthread_mutex_unlock(&queue->head_mtx);
        return EMPTY_VALUE;
    }
    Value item = atomic_load(&node->item);
    // If the queue will be empty after this pop, we make last node a dummy instead of removing it.
    if (atomic_load(&node->next) == NULL) {
        atomic_store(&node->item, EMPTY_VALUE);
        SimpleQueueNode* old_head = head;
        queue->head = node;
        free(old_head);
    } else {
        atomic_store(&head->next, atomic_load(&node->next));
        free(node);
    }
    pthread_mutex_unlock(&queue->head_mtx);
    return item;
}

bool SimpleQueue_is_empty(SimpleQueue* queue)
{
    pthread_mutex_lock(&queue->head_mtx);
    SimpleQueueNode* node = atomic_load(&queue->head->next);
    pthread_mutex_unlock(&queue->head_mtx);
    return node == NULL;
}
