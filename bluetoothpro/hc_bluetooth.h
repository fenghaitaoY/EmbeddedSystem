/**
 * create :2017.12.15
 * author :fht
 * 此文件创建主要是用于设置蓝牙模块功能
 * 广东汇承科技
 * 模块在未连接状态使用AT指令有效，连接后串口透传模式
 * 模块启动150ms，参数设置掉电不丢失
 * AT指令修改成功返回 OK，失败不返回任何信息
 * AT     检测串口是否正常工作
 * AT+DEFAULT  恢复出厂设置
 * AT+RESET    模块重启
 * AT+VERSION  获取模块版本日期
 * AT+NAME=xxx 修改蓝牙名称
 * AT+BAUD=xx,y 修改串口波特率
 * AT+CLEAR    主机清除已记录的从机地址
 * AT+LED=x    LED 开/关
 * AT+AUST=x   设置自动进入睡眠的时间
 *
 * AT指令后面不用回车换行
 */
 
 #define OK 1
 #define FAIL 0
 
 void factory_reset(char * command);
 void mode_restart(char * command);
 void change_bluetooth_name( char * command, char *name);
 void get_bluetooth_name(char *command);
 void clear_host(char *command);
 void set_led_openorclose(char *command, int mode);