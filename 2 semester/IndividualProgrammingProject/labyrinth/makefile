CC       = gcc
CFLAGS   = -c -Wall -Wextra -g -Wno-implicit-fallthrough -O2 -std=c17
LDFLAGS  =
LDFLAGS  =

.PHONY: all clean

all: labyrinth

labyrinth: main.o queue.o
	$(CC) $(LDFLAGS) -o $@ $ main.o queue.o

main.o: queue.h
	$(CC) $(CFLAGS)  main.c -o main.o

queue.o: queue.h
	$(CC) $(CFLAGS) queue.c -o queue.o

clean:
	rm -f *.o main
