/*
  名称：蓝牙串口通信显示在lcd上
  日期：2017.12.08
  修改：初版
  内容：连接好串口或者usb转串口至电脑，下载该程序，打开电源
        打开串口调试程序，将波特率设置为9600，无奇偶校验
        晶振11.0592MHz，发送和接收使用的格式相同，如都使用
        字符型格式，在发送框输入 hello ，在接收框中同样可以
		看到相同字符，说明设置和通信正确,讲SBUF收到的数据
		显示到LCD上
 */
#include<reg52.h>
#include "bluetooth.h"
#include <intrins.h>
#include <stdio.h>
#include <math.h>
#include <string.h>

/************************************************
		定义串口使用接口信息
*************************************************/
typedef struct serial_var{
		unsigned char recData;   			   //读取SBUF存储的数据
		unsigned char serial_rec_ok; 		   //串口接收成功标志
		unsigned char serial_send_ok;		   //串口发生成功标志

}serial_var_t;

serial_var_t  serial={'\0', 0, 1};

/************************************************
		定义LCD12864使用接口信息
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
"上海浩豚电子科技"  
"单片机开发板系列"
"中文字库测试程序"
"恭喜发财身体健康"
};
unsigned char code IC_DAT2[]={
"海纳百川宽容为先"
"欲成大业诚信为先"
"游弋商海济世为先"
"人立于世守法为先"
};

unsigned char temp;
#define LOG serial_write_str


/************************************************
				主程序
*************************************************/
void main()
{
		init_graphic();            //调用LCD显示图片(扩展)初始化程序
  
    DisplayGraphic(pic2);  //显示图片2
    delayms(200);
	
	  serial_init();
		
					init_lcd_characters();   		 //调用LCD字库初始化程序
          delay(100);            //大于100uS的延时程序 
          lcd_mesg(IC_DAT2);     //显示中文汉字2
          delayms(240);
	  while(1){
			if(serial.serial_rec_ok ==1){
					temp = serial_read();
					init_lcd_characters();   		 //调用LCD字库初始化程序
          delay(100);            //大于100uS的延时程序
					switch(temp){
						case 0x61: //a
							lcd_mesg(IC_DAT);      //显示中文汉字1
							break;
						case 0x62:
							clear_screen();
							break;
						case 0x63:
							lcdDisplayString(1,0,"冯海涛LOVE杨晶晶");
							break;
						case 0x64:
							lcdDisplayString(3,0,"蓝牙打开. . .");
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
		  init_graphic();            //调用LCD显示图片(扩展)初始化程序
  
          DisplayGraphic(pic2);  //显示图片2
          delayms(200);

          DisplayGraphic(pic3);  //显示图片3
          delayms(200);
					
          init_lcd_characters();   		 //调用LCD字库初始化程序
          delay(100);            //大于100uS的延时程序
          lcd_mesg(IC_DAT);      //显示中文汉字1
          delayms(240);
          delayms(240);
 	  
          init_lcd_characters();   		 //调用LCD字库初始化程序
          delay(100);            //大于100uS的延时程序 
          lcd_mesg(IC_DAT2);     //显示中文汉字2
          delayms(240);
		  delayms(240);
        }
			*/
}


/************************************************
			  串口初始化
*************************************************/
void serial_init()
{
	SCON = 0x50;    /*scon 模式1,10位数据传输方式，双机通信，允许接收外部串口数据 */
	TMOD |=0x20;    /*TMOD 定时器1，模式2,8位自动重载*/
	TH1 = 0xFD; 	/*TH1 设定重载值， 波特率9600,11.0592M晶振*/
	TR1 = 1;		/*定时器设置好，打开定时器*/
	EA = 1;			/*打开总中断*/
	ES =1;			/*打开串口中断*/
	ET1=0;			/*因为采用8位重载模式，不用中断函数设置重载值*/
}

/************************************************
			串口中断程序
*************************************************/
 void serial_interrupt(void) interrupt 4
 {

  	if(RI)
	{
		RI =0; /*RI 当接收到一帧完成，RI变为1，触发中断，需软件恢复0*/
		serial.recData = SBUF; /*读取接收缓存寄存器的值*/
		serial.serial_rec_ok = 1; /*接收成功标志*/
		if(serial.serial_rec_ok==1) /*把接收到的值再发回去，如电脑*/
		{
		 	serial_write(serial.recData);
		}
	}
	if(TI)
	{
	 	TI=0; /*TI 当发送一帧完成，TI变为1，触发中断，需软件恢复0*/
		serial.serial_send_ok = 1; /*发送成功标志*/
	}

 }

/************************************************
@fuc 从串口种读取1字节的数据返回
@return 读取的 unsigned char 类型数据
此函数只做拿取数据，调用前先判断是否接收数据完成
*************************************************/
unsigned char serial_read()
{
 	serial.serial_rec_ok = 0;
	return serial.recData;

}

/************************************************
@func 写1个字节数据到串口
@param value unsigned char 类型数据
*************************************************/
void serial_write(unsigned char value)
{
	serial.serial_send_ok = 0;
	SBUF = value;	
}

/************************************************
@func 发送1个字符串
@param str 字符串地址，确保以'\0'结尾
*************************************************/
void serial_write_str(unsigned char *str)
{
 	while(*str)
	{
	 	while(!serial.serial_send_ok); //等待前一个字符发送完成才能发送下一字节
		serial_write(*str++);

	}

}

/*****************************************************************************
-----------------------	LCD显示部分-------------------------------------------
******************************************************************************/
/************************************************
			LCD初始化字库
*************************************************/
void init_lcd_characters(void)
{
 	delay(40);    //大于40ms的延时程序
	PSB=1;		  //设置为8BIT并口工作模式
	delay(1);	  //延时
	RES=0;		  //复位
	delay(1);	  //延时
	RES=1;		  //拉高复位
	delay(10);
	TransferData(0x30,0); //  RE=0
	delay(100);
	TransferData(0x30,0); //功能设定 0011 0000
	delay(37);
	TransferData(0x08,0); //显示控制 0000 1000
	delay(100);
	TransferData(0x10,0); //光标控制 0001 0000
	delay(100);
	TransferData(0x0c,0); //显示开   0000 1100
	delay(100);
	TransferData(0x01,0); //清屏     0000 0001
	delay(10);
	TransferData(0x06,0); //Enry Mode Set ,光标从右向左加1位移动 0000 0110
	delay(100);
}

/************************************************
			液晶初始化--图形
*************************************************/
void init_graphic(void)
{
  delay(40);
  PSB=1;      //设置为8BIT并口工作模式
  delay(1);		
  RES=0;
  delay(1);
  RES=1;
  delay(10);

  TransferData(0x36,0);	 //RE =1
  delay(100);
  TransferData(0x36,0);
  delay(37);
  TransferData(0x3E,0);	 //DL 8bits RE=1，G=1
  delay(100);
  TransferData(0x01,0);	 //clear Screen
  delay(100);
}

/************************************************
		   LCD显示中文汉字
*************************************************/
void lcd_mesg(unsigned char code *adder1)
{
 	 unsigned char i;
	 unsigned char len;
	 len = strlen(adder1);
	 TransferData(0x80,0); //设置图形显示RAM地址
	 for(i=0;i<32;i++){
	 	TransferData(*adder1,1);
		adder1++;
	 }

	 TransferData(0x90,0); //设置图形显示RAM地址
	 delay(100);

	 for(i=0;i<32;i++){
	  	TransferData(*adder1,1);
		  adder1++;
	 }
}

/*************************************************
在指定行和列显示汉字
x-行数值 0-3 12864可以显示4行汉字
y-列数值0-7  12864可以显示8列汉字
一个地址对应一个汉字
0x80 - 0x87
0x90 - 0x97
0x88 - 0x8F
0x98 - 0x9F
可以显示32个汉字，64个字符
***************************************************/
void lcdDisplayString(unsigned char x, unsigned char y, unsigned char *str)
{
	unsigned char row,n=0;

	TransferData(0x30,0);  //基本指令
	TransferData(0x06,0);  //地址计数器自动累加，光标右移
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
		
		if((n+y*2) == 16) //写满一行，继续写第二行
		{
			if(x==0){
				TransferData(0x90,0);
			}else if(x==1){
				TransferData(0x88,0);
			}else if(x==2){
				TransferData(0x98,0);
			}
		}else if((n+y*2) == 32) //写满第二行，继续第三行
		{
			if(x==0){
				TransferData(0x88,0);
			}else if(x==1){
				TransferData(0x98,0);
			}
		}else if((n+y*2) == 48)  //写满第三行，继续第四行
		{
			if(x==0){
				TransferData(0x98,0);
			}
		}	
	}
	
}

/***********************************************
						清屏
************************************************/
void clear_screen()
{
    TransferData(0x01,0);
		TransferData(0x00,0);
}

/************************************************
@func 传送数据或者命令
@param DI =0 传送命令, =1 传送数据
RS=L RW=L，E=H ，写指令
RS=H RW=L, E=H , 写数据
*************************************************/
void TransferData(char data1,bit DI)
{
	lcd_check_busy();
	WRD=0;
	RS=DI;
	delay(1);
	LCD_DATA=data1;
	E=1; //E 1-0所存有效數據
	delay(1);
	E=0;
}

/************************************************
  延时 10xn毫秒程序
*************************************************/
void delayms(unsigned int n)
{
	unsigned int i,j;
	for(i=0;i<n;i++){
	 	for(j=0;j<2000;j++);
	}	
}

/************************************************
	延时程序
*************************************************/
void delay(unsigned int m)
{
	unsigned int i,j;
	for(i=0;i<m;i++){
	 	for(j=0;j<10;j++);
	}

}

/************************************************
			图像显示
*************************************************/
void DisplayGraphic(unsigned char code *adder)
{
 	int i,j;
	/************显示上半屏内容设置***********/
	for(i=0;i<32;i++){    //32行
	 	TransferData((0x80+i),0); //set vertical adder
		TransferData(0x80,0);	  //set horizontal adder
		for(j=0;j<16;j++){ //16个字节 一个char类型8位 128bit
		 	TransferData(*adder,1);
			adder++;
		}
	}
	/*************显示下半屏内容设置*********/
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
								LCD忙检测
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

































