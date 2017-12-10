/*
  ���ƣ���������ͨ����ʾ��lcd��
  ���ڣ�2017.12.08
  �޸ģ�����
  ���ݣ����Ӻô��ڻ���usbת���������ԣ����ظó��򣬴򿪵�Դ
        �򿪴��ڵ��Գ��򣬽�����������Ϊ9600������żУ��
        ����11.0592MHz�����ͺͽ���ʹ�õĸ�ʽ��ͬ���綼ʹ��
        �ַ��͸�ʽ���ڷ��Ϳ����� hello ���ڽ��տ���ͬ������
		������ͬ�ַ���˵�����ú�ͨ����ȷ,��SBUF�յ�������
		��ʾ��LCD��
 */
#include<reg52.h>
#include "bluetooth.h"
#include <intrins.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

/************************************************
		���崮��ʹ�ýӿ���Ϣ
*************************************************/
typedef struct serial_var{
		unsigned char recData;   			   //��ȡSBUF�洢������
		unsigned char serial_rec_ok; 		   //���ڽ��ճɹ���־
		unsigned char serial_send_ok;		   //���ڷ����ɹ���־

}serial_var_t;

serial_var_t  serial={'\0', 0, 1};

/************************************************
		����LCD12864ʹ�ýӿ���Ϣ
*************************************************/
sbit RS  = P2^4;
sbit WRD = P2^5;
sbit E   = P2^6;
sbit PSB = P2^1;
sbit RES = P2^3;
sbit BUSY_FLAG = P0^7;
#define LCD_DATA P0

//unsigned char code pic2[];
//unsigned char code pic3[];

unsigned char code IC_DAT[]={
"�Ϻ�������ӿƼ�"  
"��Ƭ��������ϵ��"
"�����ֿ���Գ���"
"��ϲ�������彡��"
};
unsigned char code IC_DAT2[]={
"���ɰٴ�����Ϊ��"
"���ɴ�ҵ����Ϊ��"
"��߮�̺�����Ϊ��"
"���������ط�Ϊ��"
};

unsigned char temp;
#define LOG serial_write_str


/************************************************
				������
*************************************************/
void main()
{
		init_graphic();            //����LCD��ʾͼƬ(��չ)��ʼ������
  
    DisplayGraphic(pic2);  //��ʾͼƬ2
    delayms(200);
	
	  serial_init();
		
					init_lcd_characters();   		 //����LCD�ֿ��ʼ������
          delay(100);            //����100uS����ʱ���� 
          lcd_mesg(IC_DAT2);     //��ʾ���ĺ���2
          delayms(240);
	  while(1){
			if(serial.serial_rec_ok ==1){
					temp = serial_read();
					init_lcd_characters();   		 //����LCD�ֿ��ʼ������
          delay(100);            //����100uS����ʱ����
					switch(temp){
						case 0x61: //a
							lcd_mesg(IC_DAT);      //��ʾ���ĺ���1
							break;
						case 0x62:
							clear_screen();
							break;
						case 0x63:
							lcdDisplayString(1,0,"�뺣��LOVE���");
							break;
						case 0x64:
							lcdDisplayString(3,0,"������. . .");
							break;
						case 0x65:
							lcdDisplayString(2,1,"abcdefghijklmnopqrstuvw");
							break;
						case 0x66:
							break;
						case 0x67:
							break;
								
					}
          delayms(240);
					
			}
			
		}
	  /*while(1)
	    {
		  init_graphic();            //����LCD��ʾͼƬ(��չ)��ʼ������
  
          DisplayGraphic(pic2);  //��ʾͼƬ2
          delayms(200);

          DisplayGraphic(pic3);  //��ʾͼƬ3
          delayms(200);
					
          init_lcd_characters();   		 //����LCD�ֿ��ʼ������
          delay(100);            //����100uS����ʱ����
          lcd_mesg(IC_DAT);      //��ʾ���ĺ���1
          delayms(240);
          delayms(240);
 	  
          init_lcd_characters();   		 //����LCD�ֿ��ʼ������
          delay(100);            //����100uS����ʱ���� 
          lcd_mesg(IC_DAT2);     //��ʾ���ĺ���2
          delayms(240);
		  delayms(240);
        }
			*/
}


/************************************************
			  ���ڳ�ʼ��
*************************************************/
void serial_init()
{
	SCON = 0x50;    /*scon ģʽ1,10λ���ݴ��䷽ʽ��˫��ͨ�ţ���������ⲿ�������� */
	TMOD |=0x20;    /*TMOD ��ʱ��1��ģʽ2,8λ�Զ�����*/
	TH1 = 0xFD; 	/*TH1 �趨����ֵ�� ������9600,11.0592M����*/
	TR1 = 1;		/*��ʱ�����úã��򿪶�ʱ��*/
	EA = 1;			/*�����ж�*/
	ES =1;			/*�򿪴����ж�*/
	ET1=0;			/*��Ϊ����8λ����ģʽ�������жϺ�����������ֵ*/
}

/************************************************
			�����жϳ���
*************************************************/
 void serial_interrupt(void) interrupt 4
 {

  	if(RI)
	{
		RI =0; /*RI �����յ�һ֡��ɣ�RI��Ϊ1�������жϣ�������ָ�0*/
		serial.recData = SBUF; /*��ȡ���ջ���Ĵ�����ֵ*/
		serial.serial_rec_ok = 1; /*���ճɹ���־*/
		if(serial.serial_rec_ok==1) /*�ѽ��յ���ֵ�ٷ���ȥ�������*/
		{
		 	serial_write(serial.recData);
		}
	}
	if(TI)
	{
	 	TI=0; /*TI ������һ֡��ɣ�TI��Ϊ1�������жϣ�������ָ�0*/
		serial.serial_send_ok = 1; /*���ͳɹ���־*/
	}

 }

/************************************************
@fuc �Ӵ����ֶ�ȡ1�ֽڵ����ݷ���
@return ��ȡ�� unsigned char ��������
�˺���ֻ����ȡ���ݣ�����ǰ���ж��Ƿ�����������
*************************************************/
unsigned char serial_read()
{
 	serial.serial_rec_ok = 0;
	return serial.recData;

}

/************************************************
@func д1���ֽ����ݵ�����
@param value unsigned char ��������
*************************************************/
void serial_write(unsigned char value)
{
	serial.serial_send_ok = 0;
	SBUF = value;	
}

/************************************************
@func ����1���ַ���
@param str �ַ�����ַ��ȷ����'\0'��β
*************************************************/
void serial_write_str(unsigned char *str)
{
 	while(*str)
	{
	 	while(!serial.serial_send_ok); //�ȴ�ǰһ���ַ�������ɲ��ܷ�����һ�ֽ�
		serial_write(*str++);

	}

}

/*****************************************************************************
-----------------------	LCD��ʾ����-------------------------------------------
******************************************************************************/
/************************************************
			LCD��ʼ���ֿ�
*************************************************/
void init_lcd_characters(void)
{
 	delay(40);    //����40ms����ʱ����
	PSB=1;		  //����Ϊ8BIT���ڹ���ģʽ
	delay(1);	  //��ʱ
	RES=0;		  //��λ
	delay(1);	  //��ʱ
	RES=1;		  //���߸�λ
	delay(10);
	TransferData(0x30,0); //  RE=0
	delay(100);
	TransferData(0x30,0); //�����趨 0011 0000
	delay(37);
	TransferData(0x08,0); //��ʾ���� 0000 1000
	delay(100);
	TransferData(0x10,0); //������ 0001 0000
	delay(100);
	TransferData(0x0c,0); //��ʾ��   0000 1100
	delay(100);
	TransferData(0x01,0); //����     0000 0001
	delay(10);
	TransferData(0x06,0); //Enry Mode Set ,�����������1λ�ƶ� 0000 0110
	delay(100);
}

/************************************************
			Һ����ʼ��--ͼ��
*************************************************/
void init_graphic(void)
{
  delay(40);
  PSB=1;      //����Ϊ8BIT���ڹ���ģʽ
  delay(1);		
  RES=0;
  delay(1);
  RES=1;
  delay(10);

  TransferData(0x36,0);	 //RE =1
  delay(100);
  TransferData(0x36,0);
  delay(37);
  TransferData(0x3E,0);	 //DL 8bits RE=1��G=1
  delay(100);
  TransferData(0x01,0);	 //clear Screen
  delay(100);
}

/************************************************
		   LCD��ʾ���ĺ���
*************************************************/
void lcd_mesg(unsigned char code *adder1)
{
 	 unsigned char i;
	 unsigned char len;
	 len = strlen(adder1);
	 TransferData(0x80,0); //����ͼ����ʾRAM��ַ
	 for(i=0;i<32;i++){
	 	TransferData(*adder1,1);
		adder1++;
	 }

	 TransferData(0x90,0); //����ͼ����ʾRAM��ַ
	 delay(100);

	 for(i=0;i<32;i++){
	  	TransferData(*adder1,1);
		  adder1++;
	 }
}

/*************************************************
��ָ���к�����ʾ����
x-����ֵ 0-3 12864������ʾ4�к���
y-����ֵ0-7  12864������ʾ8�к���
һ����ַ��Ӧһ������
0x80 - 0x87
0x90 - 0x97
0x88 - 0x8F
0x98 - 0x9F
������ʾ32�����֣�64���ַ�
***************************************************/
void lcdDisplayString(unsigned char x, unsigned char y, unsigned char *str)
{
	unsigned char row,n=0;

	TransferData(0x30,0);  //����ָ��
	TransferData(0x06,0);  //��ַ�������Զ��ۼӣ��������
	if(x==0){
		row = 0x80;
	}else if(x == 1){
		row = 0x90;
	}else if(x == 2){
		row = 0x88;
	}else if(x == 3){
		row = 0x98;
	}
	TransferData((row+y),0);
	while(*str != '\0'){
		TransferData(*str,1);
		str++;
		n++;
		
		if((n+y*2) == 16) //д��һ�У�����д�ڶ���
		{
			if(x==0){
				TransferData(0x90,0);
			}else if(x==1){
				TransferData(0x88,0);
			}else if(x==2){
				TransferData(0x98,0);
			}
		}else if((n+y*2) == 32) //д���ڶ��У�����������
		{
			if(x==0){
				TransferData(0x88,0);
			}else if(x==1){
				TransferData(0x98,0);
			}
		}else if((n+y*2) == 48)  //д�������У�����������
		{
			if(x==0){
				TransferData(0x98,0);
			}
		}	
	}
	
}

/***********************************************
						����
************************************************/
void clear_screen()
{
    TransferData(0x01,0);
		TransferData(0x00,0);
}

/************************************************
@func �������ݻ�������
@param DI =0 ��������, =1 ��������
RS=L RW=L��E=H ��дָ��
RS=H RW=L, E=H , д����
*************************************************/
void TransferData(char data1,bit DI)
{
	lcd_check_busy();
	WRD=0;
	RS=DI;
	delay(1);
	LCD_DATA=data1;
	E=1; //E 1-0������Ч����
	delay(1);
	E=0;
}

/************************************************
  ��ʱ 10xn�������
*************************************************/
void delayms(unsigned int n)
{
	unsigned int i,j;
	for(i=0;i<n;i++){
	 	for(j=0;j<2000;j++);
	}	
}

/************************************************
	��ʱ����
*************************************************/
void delay(unsigned int m)
{
	unsigned int i,j;
	for(i=0;i<m;i++){
	 	for(j=0;j<10;j++);
	}

}

/************************************************
			ͼ����ʾ
*************************************************/
void DisplayGraphic(unsigned char code *adder)
{
 	int i,j;
	/************��ʾ�ϰ�����������***********/
	for(i=0;i<32;i++){    //32��
	 	TransferData((0x80+i),0); //set vertical adder
		TransferData(0x80,0);	  //set horizontal adder
		for(j=0;j<16;j++){ //16���ֽ� һ��char����8λ 128bit
		 	TransferData(*adder,1);
			adder++;
		}
	}
	/*************��ʾ�°�����������*********/
	for(i=0;i<32;i++){
	 	TransferData((0x80+i),0); //set vertical adder
		TransferData(0x88,0);	  //set horizontal adder
		for(j=0;j<16;j++){
		 	TransferData(*adder,1);
			adder++;
		}
	}
}

/************************************************
								LCDæ���
*************************************************/
void lcd_check_busy()
{
		LCD_DATA = 0xff;
		RS = 0;
		WRD = 1;
		E = 1;
		while(BUSY_FLAG);
		E = 0;
}

































