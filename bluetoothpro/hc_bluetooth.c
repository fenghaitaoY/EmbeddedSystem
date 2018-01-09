#include"hc_bluetooth.h"
#include <stdio.h>
#include <string.h>
/*
 * 恢复出厂设置
 */
void factory_reset(char * command){
	strcpy(command, "AT+DEFAULT");
}

/*
 * 模块重启
 */
void mode_restart(char * command){
	strcpy(command, "AT+RESET");
}

/*
 *修改蓝牙名称
 */
void change_bluetooth_name( char * command, char *name){
	sprintf(command, "AT+NAME=%s",name);
}

/*
 *
 * 获取当前蓝牙名称
 */
void get_bluetooth_name(char *command){
	strcpy(command, "AT+NAME=?");
}

/*
 *清除已连接蓝牙设备
 */
void clear_host(char *command){
	strcpy(command, "AT+CLEAR");
}

/*
 *设置LED开关，模块复位后才生效
 */
void set_led_openorclose(char *command, int mode){
	sprintf(command, "AT+LED=%d",mode);
}










