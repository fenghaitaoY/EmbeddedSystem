LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := com_android_blue_smarthomefunc_jninative_SmartHomeNativeUtils.c \
                    linklist_func.c \
                    aes/aes.c


LOCAL_LDFLAGS := -L$(SYSROOT)/usr/lib/ -llog

LOCAL_MODULE = smarthome

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/include \
    $(LOCAL_PATH)/aes/aes.h


include $(BUILD_SHARED_LIBRARY)