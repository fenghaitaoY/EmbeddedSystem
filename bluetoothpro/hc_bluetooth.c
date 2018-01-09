#include"hc_bluetooth.h"
#include <stdio.h>
#include <string.h>
/*
 * �ָ���������
 */
void factory_reset(char * command){
	strcpy(command, "AT+DEFAULT");
}

/*
 * ģ������
 */
void mode_restart(char * command){
	strcpy(command, "AT+RESET");
}

/*
 *�޸���������
 */
void change_bluetooth_name( char * command, char *name){
	sprintf(command, "AT+NAME=%s",name);
}

/*
 *
 * ��ȡ��ǰ��������
 */
void get_bluetooth_name(char *command){
	strcpy(command, "AT+NAME=?");
}

/*
 *��������������豸
 */
void clear_host(char *command){
	strcpy(command, "AT+CLEAR");
}

/*
 *����LED���أ�ģ�鸴λ�����Ч
 */
void set_led_openorclose(char *command, int mode){
	sprintf(command, "AT+LED=%d",mode);
}










