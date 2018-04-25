$(shell rm -rf bin ; mkdir bin)
$(shell rm -rf obj ; mkdir obj)

CC:=gcc
SRC:=obj/aes.o obj/main.o

HEAD:=aes.h

bin/aes:$(SRC) $(HEAD)
	$(CC) $^ -o $@ -g -Wall

obj/aes.o:aes.c
	$(CC) -c $< -o $@ -I -g -Wall

obj/main.o:main.c
	$(CC) -c $< -o $@ -I -g -Wall

.PHONY:clean

clean:
	rm -rf bin/aes obj/*.o
