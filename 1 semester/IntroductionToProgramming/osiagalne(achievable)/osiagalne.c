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

//  Function that sets new array size.
static int more(int n)
{
    assert(n <= (INT_MAX / 3 - 1) * 2);
    return (n / 2 + 1) * 3;
}

//  Function that extends dynamic array.
static void extendarray(char **t, int *arraysize)
{
    *arraysize = more(*arraysize);
    *t = realloc(*t, (size_t) * arraysize * sizeof **t);
}

//  Function that creates new node.
static node* add_node(string* label)
{
    node* point;
    point = malloc(sizeof(node));
    point->label = label;
    point->visited = false;
    list* guard;
    //  Creating node's adj_list.
    guard = malloc(sizeof(list));
    guard->next = NULL;
    point->adj_list = guard;
    return point;
}

//  Function that adds new node to neighbourhood list of another node.
static void add_to_adj_list(list *adj_list, node *n2)
{
    list* new = malloc(sizeof(list));
    new->node = n2;
    new->next = adj_list->next;
    adj_list->next = new;
}

//  Function used to read useless input chararacters.
static void read_spaces_and_EOLs(void)
{
    int character = getchar();
    while (isalnum((char) character) == 0 && character != '}' && character != '-')
    {
        character = getchar();
    }
    ungetc((char) character, stdin);
}

//  Function that returns 0 when two char arrays are identical, -1 otherwise.
static int compare_names(const char* a, const int la, const char* b, const int lb)
{
    if (la != lb)
    {
        return -1;
    }
    else
    {
        for (int i = 0; i < la; i++)
        {
            if (a[i] != b[i])
            {
                return -1;
            }
        }
        return 0;
    }
}

//  Function that Reads node's name.
static string* ReadName(void)
{
    char* t;
    t = malloc(sizeof(char));
    int length = 0;
    int arraysize = 1;
    read_spaces_and_EOLs();
    int character = getchar();
    while (isalnum((char) character))
    {
        if (length >= arraysize)
        {
            extendarray(&t, &arraysize);
            assert(t != NULL);
        }
        t[length] = (char) character;
        length++;
        character = getchar();
    }
    ungetc((char) character, stdin);
    read_spaces_and_EOLs();
    string* w = malloc(sizeof(string));
    w->name = t;
    w->length = length;
    return w;
}

//  Function that reads one char from input and returns it.
static char see_char(void)
{
    int character = getchar();
    ungetc((char) character, stdin);
    return (char) character;
}

//  Function that browses through Nodes and returns one that is called "start".
static node* start(list *node_list)
{
    list* nodes = node_list->next;
    string* label;
    while(nodes != NULL)
    {
        label = nodes->node->label;
        if (label != NULL && compare_names(label->name, label->length, "start", 5) == 0)
        {
            return nodes->node;
        }
        nodes = nodes->next;
    }
    //  Not found one.
    return NULL;
}

//  Function that adds new node to node's list (if it's not already on the list) and returns it.
static node* add_node_to_list(string* label, list *guard)
{
    list* node_list = guard->next;
    list* prev = guard;
    bool exists = false;
    //  Searching if node already is on the list.
    while (node_list != NULL && exists == false)
    {
        if (compare_names(label->name, label->length, node_list->node->label->name, node_list->node->label->length) == 0)
        {
            exists = true;
        }
        prev = node_list;
        node_list = node_list->next;
    }
    if (exists == false)
    {
        list *new;
        new = malloc(sizeof(list));
        prev->next = new;
        new->next = NULL;
        node* new_node = add_node(label);
        new->node = new_node;
        return new->node;
    }
    else
    {
        free(label->name);
        free(label);
        return prev->node;
    }
}

//  Function that iterates through graph and uptades variable result.
static void DFS(node* n, int *result)
{
    if (n->visited == false)
    {
        n->visited = true;
        *result += 1;
        list* conn = n->adj_list->next;
        while (conn != NULL)
        {
            DFS(conn->node, result);
            conn = conn->next;
        }
    }
}

//  Function that frees allocated memory for our graph.
static void free_nodes(list* node_list)
{
    list* nodes = node_list->next;
    while (nodes != NULL)
    {
        free(nodes->node->label->name);
        free(nodes->node->label);
        while (nodes->node->adj_list != NULL)
        {
            list* aux = nodes->node->adj_list->next;
            free(nodes->node->adj_list);
            nodes->node->adj_list = aux;
        }
        free(nodes->node);
        list *aux = nodes->next;
        free(nodes);
        nodes = aux;
    }
    free(node_list);
}

//  Function that returns list of nodes read from input
static list* read_graph(void)
{
    int character = 'a';
    while (character != '{')
    {
        character = getchar();
    }
    list *guard = malloc(sizeof(list));
    guard->next = NULL;
    while (see_char() != '}')
    {
        //  Reading first node.
        string  *t = ReadName();
        node *n1 = add_node_to_list(t, guard);
        //  Reading an arrow: "->".
        getchar();
        getchar();
        //  Reading second node.
        string *t2 = ReadName();
        node *n2 = add_node_to_list(t2, guard);
        add_to_adj_list(n1->adj_list, n2);
    }
    return guard;
}

int main(void)
{
    list* nodes = read_graph();
    int result = 0;
    node *startn = start(nodes);
    DFS(startn, &result);
    printf("%d\n", result);
    free_nodes(nodes);
    return 0;
}