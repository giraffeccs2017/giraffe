
#define TSIZE 128
#define NUMCONV 8
#define PSIZE 16
#define CONVSIZE 32
#define LOGN 5
#define COPIES 512

#include "pm.h"

struct In {
    int p[COPIES][PSIZE];
    int t[COPIES][TSIZE]; // first layer of pyramid
    int t2[COPIES][TSIZE][TSIZE]; // second layer
    int omegaP[LOGN];
    int invn;
};

struct Out {
    int m[COPIES][TSIZE];
    int m2[COPIES][TSIZE];
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
    int layer2[COPIES][TSIZE];

    // pm on the first layer of pyramid
    out { 1415352216.pws x 512}

    // copy data from input into layer2 according to the match
    for (c = 0; c < COPIES; c++) {
        int idx = 0;
        int i;
        for (i = 0; i < TSIZE; i++) {
            if (matches[c][i] == 0) {
                idx = i;
            }
        }
        for (i = 0; i < TSIZE; i++) {
            layer2[c][i] = input->t2[c][idx][i];
        }
    }

    for (c = 0; c < COPIES; c++) {
        for (s = 0; s < TSIZE; s++) {
            output->m[c][s] = matches[c][s];
        }
    }

    out { 341774374.pws x 512}

    for (c = 0; c < COPIES; c++) {
        for (s = 0; s < TSIZE; s++) {
            output->m2[c][s] = matches[c][s];
        }
    }
}
