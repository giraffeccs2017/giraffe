// this is an example which evaluates the server's function in the classic
// n^{1/3} 2-server PIR protocol. Following code is based on the Woodruff-Yenkhanin
// simplest descrition possible.
//
// we assume that the database is FIXED, hardcoded so that it doesnt become part of
// the input.  We can relax this assumption if this example works out.
//
// this example is optimized for using a p=257, but we can use any field.


#define M 50

// N is the number of elements in the DB
#define N ((M)*(M-1)*(M-2)/6)

unsigned char db[N];
// we would init db to some constants

struct Input {
	unsigned short q[M];		// Raw query from client
};
struct Output {
	unsigned short r;
	unsigned short delF[M];	// partial derivates of F/del{z1..zM}
};



void compute(struct Input *input, struct Output *output) {

	// all of these operations are field operations, but to test the code
	// i selected larger field elements to avoid overflows, etc.

	unsigned short* q = input->q;
	unsigned short* df = output->delF;

	unsigned long prod2, sum;
	unsigned long i = 0, intsum;
	sum = 0;
	unsigned long DF[M];		// this can be the same as df, if all ops are in the field.

	for(int c3=2; c3<M; c3++) {
		for(int c2=1; c2<c3; c2++) {

			prod2 = (q[c3] * q[c2]);		// should be mod 257, i.e in F
			intsum = 0;

			for(int c1=0; c1<c2; c1++) {

				unsigned int t = q[c1] * db[i]; // also in the field
				intsum += t;

				DF[c3] +=  t * q[c2]; 			// in field
				DF[c2] +=  t * q[c3]; 			// ..
				DF[c1] +=  db[i] * prod2; 		// ..

				i++;
			}
			//intsum %= 257;
			sum += (intsum*prod2);		// assuming op is in field
		}
	}

	// for(int i=0; i<M; i++) {
	// 	df[i] = DF[i] % 257;		
	// }

	output->r = sum; // % 257;


}
