#include <stdio.h>
#include <stdbool.h>
#include <ctype.h>
#include <limits.h>
#include <assert.h>
#include <stdlib.h>

struct list{
    struct list* next;
    struct node* node;
};
typedef struct list list;

struct string{
    char* name;
    int length;
};
typedef struct string string;

struct node{
    string*  label;
    list* adj_list;
    bool visited;
};
typedef struct node node;

void write_nodes(list *node_list_guard)
{
    list *nodes = node_list_guard->next;
    printf("writing adj_list\n");
    while (nodes != NULL)
    {
        printf("Node ");
        write_node(nodes->node->label->name, nodes->node->label->length);
        printf(":");
        list* current = nodes->node->adj_list->next;
        while (current != NULL)
        {
            write_node(current->node->label->name, current->node->label->length);
            printf(", ");
            current = current->next;
        }
        printf("\n");
        nodes = nodes->next;
    }
}
void write_node(char *t, int n)
{
    for (int i = 0; i < n; i++)
    {
        printf("%c", t[i]);
    }
}