//
// Created by fht on 4/28/18.
//

#ifndef SMARTHOMEFUNC_SMART_H
#define SMARTHOMEFUNC_SMART_H

#define TAG "smart_home"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG , __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)


#define TRUE 0;
#define FALSE -1;

typedef struct smart_account{
    char name[128];
    char password[128];
    struct smart_acount *next;
}smart_account_t;



#endif //SMARTHOMEFUNC_SMART_H
