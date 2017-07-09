#!/usr/bin/env python
from string import Template


usage = """
./pyramids.py [text_size in bits] [pattern_size in bits] [copies in bits]
The pyramids solves:
    matching the pattern against "copies" number of texts.
    The texts are expressed in two layers of "resolution".

    On the coarse layer, the text has size "text_size",

    On the fine layer, the text has size "text_size * text_size".
    (It is divided into "text_size" blocks, each block also has size
    "text_size" and corresponds to one integer on the coarse layer.)

    The pattern matching procedure is first run against
    the coarse layer, then run against the fine layer according
    to the results of the first run.

The created c file should be put together with the pm.h file.
"""

app_template = Template(r"""
#define TSIZE $text_size
#define T2SIZE $t2_size
#define T3SIZE $t3_size
#define T4SIZE $t4_size
#define SIZE_RATIO $size_ratio
#define NUMCONV $num_of_conv
#define PSIZE $pattern_size
#define CONVSIZE $conv_size
#define LOGN $log_conv_size
#define COPIES $copies

#define RECORD_RESULT(matches, c) \
        {\
        int idx = 0;\
        int i;\
        for (i = 0; i < TSIZE; i++) {\
            if (matches[c][i] == 0) {\
                idx = i;\
            }\
        }\
        output->m[c] = (output->m[c] + idx) * SIZE_RATIO ;\
        }

#define FIND_RESULT(matches, text, layer) \
    for (c = 0; c < COPIES; c++) { \
        int idx = 0; \
        int i;\
        RECORD_RESULT(matches, c);\
        idx = output->m[c];\
        for (i = 0; i < TSIZE; i++) {\
            layer[c][i] = text[c][idx + i];\
        }\
    }

#include "pm.h"

struct In {
    int p[COPIES][PSIZE];
    int p2[COPIES][PSIZE];
    int p3[COPIES][PSIZE];
    int p4[COPIES][PSIZE];
    int t[COPIES][TSIZE]; // first layer of pyramid
    int t2[COPIES][T2SIZE]; // second layer
    int t3[COPIES][T3SIZE]; // third layer
    int t4[COPIES][T4SIZE]; // fourth layer
    int omegaP[LOGN];
    int invn;
};

struct Out {
    int m[COPIES];
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
    int accu_matches[COPIES];
    int layer[COPIES][TSIZE];

    // pm on the first layer of pyramid
    pm(input->p, input->t, matches);

    // copy data from input into layer2 according to the match
    FIND_RESULT(matches, input->t2, layer);

    pm(input->p2, layer, matches);

    FIND_RESULT(matches, input->t3, layer);

    pm(input->p3, layer, matches);

    FIND_RESULT(matches, input->t4, layer);

    pm(input->p4, layer, matches);

    for (c = 0; c < COPIES; c++) {
        RECORD_RESULT(matches, c);
    }
}
""")

if __name__ == "__main__":
    # we need: text_size in bits
    #          pattern_size in bits
    #          copies in bits
    #          (optional) size ratio in bits
    import sys
    args = sys.argv
    if len(args) != 4 and len(args) != 5:
        print usage
    else:
        t_bit = int(args[1])
        p_bit = int(args[2])
        c_bit = int(args[3])
        s_bit = t_bit
        if len(args) == 5:
            s_bit = int(args[4])
        if (t_bit <= p_bit):
            print "text size has to be larger than pattern size"
            sys.exit(1)
        t_size = 2**t_bit
        p_size = 2**p_bit
        copies = 2**c_bit
        num_of_conv = t_size/p_size
        conv_size = p_size * 2
        logn = p_bit + 1
        size_ratio = 2**s_bit
        t2_size = size_ratio * t_size
        t3_size = size_ratio * t2_size
        t4_size = size_ratio * t3_size
        print app_template.substitute(
            text_size=t_size,
            num_of_conv=num_of_conv,
            pattern_size=p_size,
            conv_size=conv_size,
            log_conv_size=logn,
            copies=copies, size_ratio=size_ratio,
            t2_size=t2_size,
            t3_size=t3_size,
            t4_size=t4_size,
            )
