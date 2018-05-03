//
// Created by fht on 4/28/18.
//

#ifndef SMARTHOMEFUNC_LINKLIST_FUNC_H
#define SMARTHOMEFUNC_LINKLIST_FUNC_H


#include "smart.h"

int init_link(smart_account_t *head);
void load(smart_account_t *head, char *path);
int save(smart_account_t *p, char *path);
int insert_tail(smart_account_t *head, smart_account_t *node);
int length(smart_account_t *head);
void destroy(smart_account_t **head);
int do_verify(smart_account_t * head, char *name, char *password);


#endif //SMARTHOMEFUNC_LINKLIST_FUNC_H
