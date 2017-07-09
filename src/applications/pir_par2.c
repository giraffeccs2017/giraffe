// this is an example which evaluates the server's function in the classic
// n^{1/3} 2-server PIR protocol. Following code is based on the Woodruff-Yenkhanin
// simplest descrition possible.
//
// we assume that the database is FIXED, hardcoded so that it doesnt become part of
// the input.  We can relax this assumption if this example works out.
//
// this example is optimized for using a p=257, but we can use any field.

/*
 * YJ: This is an optimized version of pir.c
 */

#define DIV(x,y) (1 + ((x)-1)/(y))
#define ADDER(a, len) \
{ \
    int curlen = len; \
    int bb = 1;\
    if (len == 1) { \
        bb = 0; \
    } \
    int step; \
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
#define M 40

// N is the number of elements in the DB
#define N 9880 //((M)*(M-1)*(M-2)/6)
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
        int intsum;
	int sum = 0;
	//int DF[M];		// this can be the same as df, if all ops are in the field.

        int j = 0;
        int sum_a[N];

        int c3, c2, c1;
        int idx[M];
        int s[M][N];
        for (c3 = 0; c3 < M; c3++) {
            idx[c3] = 0;
        }
	for(c3=2; c3<M; c3++) {
		for(c2=1; c2<c3; c2++) {

			prod2 = (input->q[c3] * input->q[c2]);		// should be mod 257, i.e in F
			int int_sum;
                        int t[M];

			for(c1=0; c1<c2; c1++) {
                                int tt = input->q[c1] * input->db[i];

				t[c1] = tt; // also in the field

                                s[c3][idx[c3]] = tt*input->q[c2];
                                idx[c3] = idx[c3] + 1;

                                s[c2][idx[c2]] = tt*input->q[c3];
                                idx[c2] = idx[c2] + 1;

                                s[c1][idx[c1]] = input->db[i]*prod2;
                                idx[c1] = idx[c1]+1;

				i++;
			}
                        ADDER(t, c1);
                        //int_sum = t[0];
			//intsum %= 257;
                        sum_a[j] = t[0] * prod2;
                        j++;
		}
	}
        for (c3 = 0; c3 < M; c3++) {
            if (idx[c3] > 0) {
                ADDER(s[c3], idx[c3]);
                output->delF[c3] += s[c3][0];
            }
        }

	// for(int i=0; i<M; i++) {
	// 	df[i] = DF[i] % 257;		
	// }
        ADDER(sum_a, j);

	output->r = sum_a[0]; // % 257;


}
