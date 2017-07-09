#include <stdlib.h>
#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <limits.h>

int main() {
	int fd;
	fd=open("/dev/urandom",O_RDONLY );
	if (fd<0) {
		fprintf(stderr,"Could not open urandom");
		exit(1);
	}
	double d;
	unsigned long l;
	for(int i=0; i<4000; i++) {
		read(fd,&l, sizeof(long));
		if (i%8==0) { fprintf(stdout,"\n\t"); }	
		fprintf(stdout,"%.8f, ", l/(double)ULONG_MAX);	
	}
	close(fd);
}
