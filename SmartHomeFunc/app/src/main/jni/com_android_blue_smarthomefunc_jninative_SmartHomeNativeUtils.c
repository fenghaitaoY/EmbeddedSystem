//
// Created by fht on 4/28/18.
//

#include <stdio.h>
#include <android/log.h>
#include <string.h>
#include <malloc.h>

#include "com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils.h"
#include "include/smart.h"
#include "include/linklist_func.h"



smart_account_t *head;

char g_path[128];

char *secret_key = "abcdefghijklmnop";


JNIEXPORT jint JNICALL Java_com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils_initPath
  (JNIEnv *env, jclass jcls, jstring  jstr_path){
    LOGD("start");
    jint ret = 0;
    const char *path;
    jboolean isCopy;
    path = (*env)->GetStringUTFChars(env, jstr_path, &isCopy);

    strcpy (g_path, path);
    head = malloc (sizeof (smart_account_t));
    //初始存储路径
    init_link (head);
    load (head, path);

    (*env)->ReleaseStringUTFChars(env, jstr_path, path);

    LOGD ("end");
    return ret;
 }


JNIEXPORT jint JNICALL Java_com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils_save
  (JNIEnv *env, jclass jcls, jstring jstr_name, jstring jstr_password){
    jint ret = 0;
    LOGD ("start");
    jboolean is_name, is_password;
    const char *name;
    const char *password;

    smart_account_t *temp=NULL;

    name = (*env)->GetStringUTFChars(env, jstr_name, &is_name);
    password = (*env)->GetStringUTFChars(env, jstr_password, &is_password);

    LOGD (" save , name = %s, password = %s", name, password);
    //保存用户, 密码
    if (NULL == head){ init_link (head);}
    LOGD ("  init link end");
    temp = malloc (sizeof (smart_account_t));
    strcpy (temp->name, name);
    strcpy (temp->password, password);
    LOGD (" insert tail");
    insert_tail (head, temp);

    LOGD (" do save ");
    temp = head;
    while (temp != NULL){
        LOGD ("name = %s", temp->name);
        temp = temp->next;
    }
    ret = save (head, g_path);

    (*env)->ReleaseStringUTFChars(env, jstr_name, name);
    (*env)->ReleaseStringUTFChars(env, jstr_password, password);

    free (temp);
    LOGD ("end");

    return ret;
  }


JNIEXPORT jint JNICALL Java_com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils_verify
  (JNIEnv *env, jclass jcls, jstring jstr_name, jstring jstr_password){
    jint ret = 0;
    LOGD ("start");
    jboolean is_name, is_password;
    const char *name;
    const char *password;

    name = (*env)->GetStringUTFChars(env, jstr_name, &is_name);
    password = (*env)->GetStringUTFChars(env, jstr_password, &is_password);
    smart_account_t * cur;
    cur = head;
    while (cur != NULL){
        //LOGD ("%s: name = %s", __FUNCTION__, cur->name);
        cur = cur->next;
    }
    //验证用户, 密码
    ret = do_verify (head, name, password);

    (*env)->ReleaseStringUTFChars(env, jstr_name, name);
    (*env)->ReleaseStringUTFChars(env, jstr_password, password);
    LOGD ("end, ret = %d ", ret);

    return ret;
  }


JNIEXPORT jint JNICALL Java_com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils_destroy
        (JNIEnv *env , jclass jcls){
    LOGD ("start");
    //销毁链表, 释放空间
    destroy (head);
    LOGD ("end");
    return TRUE;
}