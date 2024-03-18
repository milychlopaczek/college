#include <malloc.h>
#include <stdatomic.h>
#include <stdbool.h>

#include "HazardPointer.h"
#include "LLQueue.h"

struct LLNode;
typedef struct LLNode LLNode;
typedef _Atomic(LLNode*) AtomicLLNodePtr;

struct LLNode {
    AtomicLLNodePtr next;
    Value item;
};

LLNode* LLNode_new(Value item)
{
    LLNode* node = (LLNode*)malloc(sizeof(LLNode));
    if(node == NULL) {
        return NULL;
    }
    atomic_init(&node->next, NULL);
    atomic_init(&node->item, item);
    return node;
}

struct LLQueue {
    AtomicLLNodePtr head;
    AtomicLLNodePtr tail;
    HazardPointer hp;
};

LLQueue* LLQueue_new(void)
{
    LLQueue* queue = (LLQueue*)malloc(sizeof(LLQueue));
    if(queue == NULL) {
        return NULL;
    }
    LLNode* dummy = LLNode_new(EMPTY_VALUE);
    queue->head = dummy;
    queue->tail = dummy;
    HazardPointer_initialize(&queue->hp);
    return queue;
}

void LLQueue_delete(LLQueue* queue)
{
    LLNode* node = atomic_load(&queue->head);
    while (node != NULL) {
        LLNode* next = node->next;
        free(node);
        node = next;
    }
    HazardPointer_finalize(&queue->hp);
    free(queue);
}

void LLQueue_push(LLQueue* queue, Value item)
{
    LLNode* new_node = LLNode_new(item);
    while (true) {
        LLNode* tail = atomic_load(&queue->tail); // Step 1
        LLNode* expected = NULL;
        if (atomic_compare_exchange_strong(&tail->next, &expected, new_node)) {
            atomic_store(&queue->tail, new_node);
            return;
        }
    }
}

Value LLQueue_pop(LLQueue* queue)
{
    while (true) {
        LLNode* head = HazardPointer_protect(&queue->hp, (const _Atomic(void*)*)&queue->head); // Step 1
        Value item = atomic_load(&head->item);
        if (atomic_compare_exchange_strong(&head->item, &item, EMPTY_VALUE)) { // Step 2
            if (item != EMPTY_VALUE) { // Step 3a
                LLNode* next = atomic_load(&head->next);
                if (next != NULL && atomic_compare_exchange_strong(&queue->head, &head, next)) {
                    //HazardPointer_retire(&queue->hp, head);
                }
                HazardPointer_clear(&queue->hp);
                return item;
            } else { // Step 3b
                LLNode* next = atomic_load(&head->next);
                if (next == NULL) {
                    HazardPointer_clear(&queue->hp);
                    return EMPTY_VALUE;
                } else {
                    if (atomic_compare_exchange_strong(&queue->head, &head, next)) {
                        //HazardPointer_retire(&queue->hp, head);
                    }
                }
            }
        }
    }
}

bool LLQueue_is_empty(LLQueue* queue)
{
    LLNode* head = atomic_load(&queue->head);
    LLNode* next = atomic_load(&head->next);
    return next == NULL;
}
