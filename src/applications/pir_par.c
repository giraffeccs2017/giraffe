// this is an example which evaluates the server's function in the classic
// n^{1/3} 2-server PIR protocol. Following code is based on the Woodruff-Yenkhanin
// simplest descrition possible.
//
// we assume that the database is FIXED, hardcoded so that it doesnt become part of
// the input.  We can relax this assumption if this example works out.
//
// this example is optimized for using a p=257, but we can use any field.
//


/*
 * YJ: 
 * This source code is a bit hard to follow.
 * And this is an optimized version of pir_par2.c: this saves about 50% of the
 * multiplication gates.
 * Maybe read pir_par2.c first.
 */

#define DIV(x,y) (1 + ((x)-1)/(y))
#define ADDER(a, len) \
{ \
    int curlen = len; \
    int bb = 1;\
    if (len == 1) { \
        bb = 0; \
    } \
    for (step = DIV(len,2); bb == 1; step = DIV(curlen,2)) {\
        int adder_i; \
            for (adder_i = 0; adder_i < step; adder_i++) { \
                if (adder_i + step < curlen) {\
                    a[adder_i] = a[adder_i] + a[adder_i + step];\
                }\
            }\
        curlen = step;\
            if (step == 1) {\
                bb = 0;\
            }\
    }\
}

#define M 30

// N is the number of elements in the DB
#define N  4060 //((M)*(M-1)*(M-2)/6)

// we would init db to some constants

struct Input {
    int q[M];		// Raw query from client
    int db[N];
};
struct Output {
    int r;
    int delF[M];	// partial derivates of F/del{z1..zM}
};



void compute(struct Input *input, struct Output *output) {

    // all of these operations are field operations, but to test the code
    // i selected larger field elements to avoid overflows, etc.

    //unsigned short* q = input->q;
    //unsigned short* df = output->delF;

    int prod2;
    int i = 0; 
    int sum = 0;
    //int DF[M];		// this can be the same as df, if all ops are in the field.

    int step = 0;
    int c3, c2, c1;
    int ss[M];
    int ss1[M][N];
    int idx[M];
    int idx2[M];
    int ss2[M][N];
    for (c3 = 0; c3 < M; c3++) {
        idx[c3] = 0;
        idx2[c3] = 0;
    }
    i = 0;
    for(c3=2; c3<M; c3++) {
        int insum_prod2[M];
        int insum_qc2[M];
        for(c2=1; c2<c3; c2++) {

            prod2 = (input->q[c3] * input->q[c2]);		// should be mod 257, i.e in F

            int t[M];


            for(c1=0; c1<c2; c1++) {

                t[c1] = input->q[c1] * input->db[i]; // also in the field
                //            output->delF[c1] +=  input->db[i] * prod2; 		// ..
                ss1[c1][idx[c1]] = input->db[i] * prod2;
                idx[c1] += 1; // (c3, c2)

                i++;
            }
            ADDER(t, c2);
            insum_qc2[c2] = t[0] * input->q[c2];
            //output->delF[c3] += intsum * input->q[c2];
            //output->delF[c2] += t[0] * input->q[c3];
            ss2[c2][idx2[c2]] = t[0] * input->q[c3];
            idx2[c2] += 1;

            //intsum %= 257;
            insum_prod2[c2] = (t[0]*prod2);		// assuming op is in field
        }
        insum_prod2[0] = 0;
        insum_qc2[0] = 0;
        ADDER(insum_prod2, c3);
        ADDER(insum_qc2, c3);
        ss[c3] = insum_prod2[0];// ss[c3] = sum(insum_prod2)
        output->delF[c3] += insum_qc2[0];// output->delF[c3] += sum(insum_qc[c2])
    }
    for (c3 = 0; c3 < M; c3++) {
        if (idx[c3] > 0) {
            ADDER(ss1[c3], idx[c3]);
            output->delF[c3] += ss1[c3][0];
        }
        if (idx2[c3] > 0) {
            ADDER(ss2[c3], idx2[c3]);
            output->delF[c3] += ss2[c3][0];
        }
    }
    ss[0] = 0; ss[1] = 0;
    ADDER(ss, M);
    //sum = ss[0]; // sum = sum(ss)

    // for(int i=0; i<M; i++) {
    // 	df[i] = DF[i] % 257;		
    // }

    output->r = ss[0]; // % 257;


}
