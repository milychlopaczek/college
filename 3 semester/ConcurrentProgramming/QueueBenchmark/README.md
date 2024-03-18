The task is to implement several non-blocking queue implementations with multiple readers and writers: SimpleQueue, RingsQueue, LLQueue, BLQueue. Two implementations use regular mutexes, and the next two use atomic operations, including the key compare_exchange.

In each case, the implementation consists of the <queue> structure and methods:

<queue>* <queue>_new(void) - allocates (malloc) and initializes a new queue.
void <queue>_delete(<queue>* queue) - frees any memory allocated by the queue methods.
void <queue>_push(<queue>* queue, Value value) - adds a value to the end of the queue.
Value <queue>_pop(<queue>* queue) - retrieves a value from the beginning of the queue or returns EMPTY_VALUE if the queue is empty.
bool <queue>_is_empty(<queue>* queue) - checks if the queue is empty.

