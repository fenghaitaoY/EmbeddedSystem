/**
 * create :2017.12.15
 * author :fht
 * ���ļ�������Ҫ��������������ģ�鹦��
 * �㶫��пƼ�
 * ģ����δ����״̬ʹ��ATָ����Ч�����Ӻ󴮿�͸��ģʽ
 * ģ������150ms���������õ��粻��ʧ
 * ATָ���޸ĳɹ����� OK��ʧ�ܲ������κ���Ϣ
 * AT     ��⴮���Ƿ���������
 * AT+DEFAULT  �ָ���������
 * AT+RESET    ģ������
 * AT+VERSION  ��ȡģ��汾����
 * AT+NAME=xxx �޸���������
 * AT+BAUD=xx,y �޸Ĵ��ڲ�����
 * AT+CLEAR    ��������Ѽ�¼�Ĵӻ���ַ
 * AT+LED=x    LED ��/��
 * AT+AUST=x   �����Զ�����˯�ߵ�ʱ��
 *
 * ATָ����治�ûس�����
 */
 
 #define OK 1
 #define FAIL 0
 
 void factory_reset(char * command);
 void mode_restart(char * command);
 void change_bluetooth_name( char * command, char *name);
 void get_bluetooth_name(char *command);
 void clear_host(char *command);
 void set_led_openorclose(char *command, int mode);