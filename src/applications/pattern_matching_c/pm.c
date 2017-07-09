
#define TSIZE 128
#define NUMCONV 8
#define PSIZE 16
#define CONVSIZE 32
#define LOGN 5
#define COPIES 512

#include "pm.h"

struct In {
    int p[COPIES][PSIZE];
    int t[COPIES][TSIZE];
    int omegaP[LOGN];
    int doit;
    int invn;
};

struct Out {
    int y[COPIES][TSIZE];
};


void compute(struct In *input, struct Out *output) {
    int s;
    int c, q;
    int tmp;
    int p2fft[CONVSIZE]; // p^2 padded with 0
    int pfft[CONVSIZE]; // t padded with 0
    int p3tmp[PSIZE]; // p^3
    int t2fft[NUMCONV][CONVSIZE]; // t^2 padded with 0
    int tfft[NUMCONV][CONVSIZE]; // t padded with 0
    int matches[COPIES][TSIZE];
    pm(input->p, input->t, matches);

    for (c = 0; c < COPIES; c++) {
        for (s = 0; s < TSIZE; s++) {
            output->y[c][s] = matches[c][s];
        }
    }
}
