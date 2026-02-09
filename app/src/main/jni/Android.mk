LOCAL_PATH := $(call my-dir)

ROOT_PATH := $(LOCAL_PATH)/../../../../..
ENGINE_PATH := $(ROOT_PATH)/engine/engine
NANOGL_PATH := $(ROOT_PATH)/engine/nanogl
SDL_PATH := $(ROOT_PATH)/engine/SDL2
HLSDK_PATH := $(ROOT_PATH)/client
XASH3D_CONFIG := $(LOCAL_PATH)/xash3d_config.mk

include $(CLEAR_VARS)

LOCAL_MODULE := NanoGL
LOCAL_SRC_FILES := \
	$(NANOGL_PATH)/nanogl.cpp \
	$(NANOGL_PATH)/nanoWrap.cpp \
	$(NANOGL_PATH)/eglwrap.cpp

LOCAL_C_INCLUDES := \
	$(NANOGL_PATH) \
	$(NANOGL_PATH)/GL

LOCAL_CFLAGS += -D__MULTITEXTURE_SUPPORT__

LOCAL_CPPFLAGS += -std=gnu++11

include $(BUILD_STATIC_LIBRARY)

include $(ENGINE_PATH)/Android.mk

include $(ROOT_PATH)/engine/mainui/Android.mk
include $(ROOT_PATH)/client/cl_dll/Android.mk
include $(ROOT_PATH)/client/regamedll/regamedll/Android.mk
