#include <malloc.h>
#include <stdatomic.h>
#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <threads.h>

#include "HazardPointer.h"

thread_local int _thread_id = -1;
int _num_threads = -1;

void HazardPointer_register(int thread_id, int num_threads)
{
    _thread_id = thread_id;
    _num_threads = num_threads;
}

void HazardPointer_initialize(HazardPointer* hp)
{
    // Initialize retired array.
    for (int i = 0; i < MAX_THREADS; ++i) {
        hp->retired[i] = malloc(MAX_THREADS * sizeof(_Atomic(void*)));
        atomic_store(&hp->len_retired[i], MAX_THREADS);
    }
    for (int i = 0; i < MAX_THREADS; ++i) {
        atomic_store(&hp->pointer[i], NULL);
        for (int j = 0; j < MAX_THREADS; ++j) {
            atomic_store(&hp->retired[i][j], NULL);
        }
    }
}

void HazardPointer_finalize(HazardPointer* hp)
{
    // Free the reserved addresses.
    for (int i = 0; i < _num_threads; ++i) {
        for (int j = 0; j < hp->len_retired[i]; ++j) {
            void* ptr = atomic_load(&hp->retired[i][j]);
            if (ptr != NULL) {
                free(ptr);
                atomic_store(&hp->retired[i][j], NULL);
            }
        }
        atomic_store(&hp->pointer[i], NULL);
    }
    // Free the retired array.
    for (int i = 0; i < MAX_THREADS; ++i) {
        free(hp->retired[i]);
    }
}

void* HazardPointer_protect(HazardPointer* hp, const _Atomic(void*)* atom)
{
    void* ptr;
    while (true) {
        ptr = atomic_load(atom);
        atomic_store(&hp->pointer[_thread_id], ptr);
        if (atomic_load(&hp->pointer[_thread_id]) == ptr) {
            break;
        }
    }
    return ptr;
}

void HazardPointer_clear(HazardPointer* hp)
{
    atomic_store(&hp->pointer[_thread_id], NULL);
}

void HazardPointer_retire(HazardPointer* hp, void* ptr)
{
    // Add ptr to retired addresses.
    for (int i = 0; i < hp->len_retired[_thread_id]; ++i) {
        if (atomic_load(&hp->retired[_thread_id][i]) == NULL) {
            atomic_store(&hp->retired[_thread_id][i], ptr);
            return;
        }
        if (i + 1 == hp->len_retired[_thread_id]) {
            printf("resize\n");
            // Resize the retired array.
            atomic_store(&hp->len_retired[_thread_id], hp->len_retired[_thread_id] * 2);
            hp->retired[_thread_id] = realloc(hp->retired[_thread_id], hp->len_retired[_thread_id] * sizeof(_Atomic(void*)));
            for (int j = i + 1; j < hp->len_retired[_thread_id]; ++j) {
                atomic_store(&hp->retired[_thread_id][j], NULL);
            }
        }
    }

    // Count the retired addresses:
    int count = 0;
    for (int i = 0; i < _num_threads; ++i) {
        for (int j = 0; j < hp->len_retired[i]; ++j) {
            if (atomic_load(&hp->retired[i][j]) != NULL) {
                count++;
            }
        }
    }

    if (count >= RETIRED_THRESHOLD) {
        // Free the retired addresses that are not protected.
        printf("count >= RETIRED_THRESHOLD: Freeing retired addresses\n");
        for (int i = 0; i < hp->len_retired[i]; ++i) {
            void* ptr = atomic_load(&hp->retired[_thread_id][i]);
            if (ptr != NULL) {
                bool protected = false;
                for (int k = 0; k < _num_threads; ++k) {
                    if (atomic_load(&hp->pointer[k]) == ptr) {
                        protected = true;
                        break;
                    }
                }
                if (!protected) {
                    free(ptr);
                    atomic_store(&hp->retired[_thread_id][i], NULL);
                }
            }
        }
    }
}
