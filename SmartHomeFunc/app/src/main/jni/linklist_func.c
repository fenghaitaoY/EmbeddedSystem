#include "linklist_func.h"
#include <stdint.h>
#include <stdio.h>
#include <malloc.h>
#include <string.h>
#include <android/log.h>
#include "aes/aes.h"
//
// Created by fht on 4/28/18.
//

/**
 * 初始链表
 * @param head
 * @return
 */
extern char *secret_key;

int init_link(smart_account_t *head){
    if(NULL == head){
        return FALSE;
    }
    head->next = NULL;

    return TRUE;
}

/**
 * 加载本地数据
 * @param head
 * @param path
 */
void load(smart_account_t *head, char *path){
    FILE *fp = NULL;
    smart_account_t *node = NULL;

    fp = fopen (path,"r");

    if (NULL == fp){
        return;
    }

    node = malloc(sizeof (smart_account_t));

    if (NULL == node){
        fclose (fp);
        return;
    }

    while (fread (node, sizeof(smart_account_t), 1, fp) != 0){
        //插链表
        insert_tail (head, node);
        node = malloc (sizeof (smart_account_t));
        if (NULL == node){
            fclose (fp);
            return;
        }
    }

    free(node);
    fclose (fp);

}

/**
 * 保存用户
 * @param p
 * @param path
 * @return
 */
int save(smart_account_t *p, char *path){
    FILE *fp = NULL;
    smart_account_t *cur = NULL;
    LOGD (" %s : start ", __FUNCTION__);
    fp = fopen (path, "w");
    if (NULL == fp){
        return FALSE;
    }

    LOGD (" %s : path= %s , P = %p", __FUNCTION__, path, p);
    if (NULL == p){ return FALSE;}
    cur = p->next;
    while (cur != NULL){
        LOGD ("name = %s, password = %s", cur->name, cur->password);
        fwrite (cur, sizeof (smart_account_t), 1, fp);
        cur = cur->next;
    }
    fclose (fp);
    return TRUE;
}

/**
 * 尾插入法
 * @param head
 * @param node
 * @return
 */
int insert_tail(smart_account_t *head, smart_account_t *node){
    smart_account_t *tail = NULL;
    if (NULL == node || NULL == head){
        return FALSE;
    }
    tail = head;
    LOGD ("insert tail start");
    while (tail->next != NULL){
        tail = tail->next;
    }

    tail->next = node;
    node->next = NULL;
    return TRUE;
}

/**
 * 用户总数
 * @param head
 * @return
 */
int length(smart_account_t *head){
    int ret =0;

    smart_account_t * current= NULL;
    current = head->next;
    while (current != NULL){
        ret++;
        current = current->next;
    }
    return ret;
}

/**
 * 销毁链表
 * @param head
 */
void destroy(smart_account_t **head){
    smart_account_t *del = NULL, *temp = NULL;

    del = *head;
    while (del != NULL){
        temp = del->next;
        free (del);
        del = temp;
    }
    *head = NULL;
}



int do_verify(smart_account_t * head, char *name, char *password){
    smart_account_t *current = NULL;

    if (name == NULL || password == NULL){
        return FALSE;
    }

    current = head->next;

    while (current != NULL){
        LOGD ("name = %s", current->name);
        if (strcmp(name, current->name)== 0 && strcmp(password, current->password) == 0){
            return TRUE;
        }
        current = current->next;
    }

    return FALSE;

}