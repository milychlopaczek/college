#include <malloc.h>
#include <stdatomic.h>
#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "BLQueue.h"
#include "HazardPointer.h"

struct BLNode;
typedef struct BLNode BLNode;
typedef _Atomic(BLNode*) AtomicBLNodePtr;

struct BLNode {
    AtomicBLNodePtr next;
    _Atomic(Value) items[BUFFER_SIZE];
    _Atomic(size_t) push_idx;
    _Atomic(size_t) pop_idx;
};

BLNode* BLNode_new(void)
{
    BLNode* node = (BLNode*)malloc(sizeof(BLNode));
    atomic_init(&node->next, NULL);
    atomic_init(&node->push_idx, 0);
    atomic_init(&node->pop_idx, 0);
    for (size_t i = 0; i < BUFFER_SIZE; i++) {
        atomic_init(&node->items[i], EMPTY_VALUE);
    }
    return node;
}

struct BLQueue {
    AtomicBLNodePtr head;
    AtomicBLNodePtr tail;
    HazardPointer hp;
};

BLQueue* BLQueue_new(void)
{
    BLQueue* queue = (BLQueue*)malloc(sizeof(BLQueue));
    if(queue == NULL) {
        return NULL;
    }
    BLNode* new = BLNode_new();
    queue->head = new;
    queue->tail = new;
    HazardPointer_initialize(&queue->hp);
    return queue;
}

void BLQueue_delete(BLQueue* queue)
{
    HazardPointer_finalize(&queue->hp);
    BLNode* node;
    atomic_store(&node, queue->head);
    while (node != NULL) {
        BLNode* next = atomic_load(&node->next);
        free(node);
        node = next;
    }
    free(queue);
}

void BLQueue_push(BLQueue* queue, Value item)
{
    while (true) {
        BLNode* tail = HazardPointer_protect(&queue->hp, (const _Atomic(void*)*)&queue->tail); // Step 1
        _Atomic(size_t) idx = atomic_fetch_add(&tail->push_idx, 1); // Step 2
        if (idx < BUFFER_SIZE) { // Step 3a
            Value expected = EMPTY_VALUE;
            if (atomic_compare_exchange_strong(&tail->items[idx], &expected, item)) {
                HazardPointer_clear(&queue->hp);
                return;
            } else {
                continue;
            }
        } else { // Step 3b
            BLNode* tail_next = atomic_load(&tail->next);
            if (tail_next != NULL) { // Step 4a
                continue;
            } else { // Step 4b
                BLNode* new = BLNode_new();
                new->push_idx = 1;
                new->items[0] = item;
                if (atomic_compare_exchange_strong(&queue->tail, &tail, new)) {
                    atomic_store(&tail->next, new);
                    HazardPointer_clear(&queue->hp);
                    return;
                } else {
                    free(new);
                }
            }
        }
    }
}

Value BLQueue_pop(BLQueue* queue)
{
    while (true) {
        BLNode* head = HazardPointer_protect(&queue->hp, (const _Atomic(void*)*)&queue->head); // Step 1
        _Atomic(size_t) pop_idx = atomic_fetch_add(&head->pop_idx, 1); // Step 2
        if (pop_idx < BUFFER_SIZE) { // Step 3a
            Value expected = EMPTY_VALUE;
            if (atomic_compare_exchange_strong(&head->items[pop_idx], &expected, TAKEN_VALUE)) {
                continue;
            } else {
                HazardPointer_clear(&queue->hp);
                return expected;
            }
        } else { // Step 3b
            BLNode* head_next = atomic_load(&head->next);
            if (head_next != NULL) {
                if (atomic_compare_exchange_strong(&queue->head, &head, head_next)) {
                    HazardPointer_retire(&queue->hp, head);
                }
            } else {
                HazardPointer_clear(&queue->hp);
                return EMPTY_VALUE;
            }
        }
    }
}

bool BLQueue_is_empty(BLQueue* queue)
{
    BLNode* head = HazardPointer_protect(&queue->hp, (const _Atomic(void*)*)&queue->head); // Step 1
    if (head->next == NULL) {
        _Atomic(size_t) pop_idx = atomic_load(&head->pop_idx);
        _Atomic(size_t) push_idx = atomic_load(&head->push_idx);
        for (size_t i = pop_idx; i < push_idx; i++) {
            if (atomic_load(&head->items[i]) != EMPTY_VALUE) {
                HazardPointer_clear(&queue->hp);
                return false;
            }
        }
        HazardPointer_clear(&queue->hp);
        return true;
    }
    HazardPointer_clear(&queue->hp);
    return false;
}
