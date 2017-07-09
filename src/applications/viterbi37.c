/* 

This is a basic Viterbi decoding algorithm for the K=7 rate 1/3 classic 
code.  The code is implemented using int operations suitable for the
pipeline and using hardcoded  decoding parameters for a gaussian noise model.  

Part of this code was taken from Phil Karn's KA9Q implementation, v3.0.1.

*/

// number of message bits
#define NBITS   1152
#define NBYTES  (NBITS/8)


struct Input {
	unsigned char *symbols;	// Raw deinterleaved input symbols
};
struct Output {
	unsigned long metric;	// Final path metric (returned value)
	unsigned char *data;	// Decoded output data
};





// these are the decoding metrics, i.e. the path lengths for the trellis
const int table[][256] = { 
{   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   0,   0,   0,   0,   0,   0,   0,   0,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -2,  -2,  -2,  -2,  -2,  -2,  -3,  -3,  -3,  -3,  -3,  -3,  -4,  -4,  -4,  -4,  -4,  -5,  -5,  -5,  -5,  -5,  -6,  -6,  -6,  -6,  -6,  -7,  -7,  -7,  -7,  -7,  -8,  -8,  -8,  -8,  -8,  -9,  -9,  -9,  -9,  -9, -10, -10, -10, -10, -11, -11, -11, -11, -11, -12, -12, -12, -12, -13, -13, -13, -13, -14, -14, -14, -14, -14, -15, -15, -15, -15, -16, -16, -16, -16, -17, -17, -17, -17, -18, -18, -18, -18, -19, -19, -19, -19, -19, -20, -20, -20, -20, -21, -21, -21, -21, -22, -22, -22, -22, -23, -23, -23, -23, -24, -24, -24, -24, -25, -25, -25, -25, -25, -26, -26, -26, -26, -27, -34, }, 
{ -34, -27, -27, -26, -26, -26, -26, -25, -25, -25, -25, -25, -24, -24, -24, -24, -23, -23, -23, -23, -22, -22, -22, -22, -21, -21, -21, -21, -20, -20, -20, -20, -19, -19, -19, -19, -19, -18, -18, -18, -18, -17, -17, -17, -17, -16, -16, -16, -16, -15, -15, -15, -15, -14, -14, -14, -14, -14, -13, -13, -13, -13, -12, -12, -12, -12, -11, -11, -11, -11, -11, -10, -10, -10, -10,  -9,  -9,  -9,  -9,  -9,  -8,  -8,  -8,  -8,  -8,  -7,  -7,  -7,  -7,  -7,  -6,  -6,  -6,  -6,  -6,  -5,  -5,  -5,  -5,  -5,  -4,  -4,  -4,  -4,  -4,  -3,  -3,  -3,  -3,  -3,  -3,  -2,  -2,  -2,  -2,  -2,  -2,  -1,  -1,  -1,  -1,  -1,  -1,  -1,  -1,   0,   0,   0,   0,   0,   0,   0,   0,   1,   1,   1,   1,   1,   1,   1,   1,   1,   1,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   2,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   3,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4,   4, }
};


// generator polynomials for the NASA Standard K=7 rate 1/3 code
#define	POLYA	0x4f
#define POLYB	0x57
#define	POLYC	0x6d


/* The basic Viterbi decoder operation, called a "butterfly"
 * operation because of the way it looks on a trellis diagram. Each
 * butterfly involves an Add-Compare-Select (ACS) operation on the two nodes
 * where the 0 and 1 paths from the current node merge at the next step of
 * the trellis.
 *
 * The code polynomials are assumed to have 1's on both ends. Given a
 * function encode_state() that returns the two symbols for a given
 * encoder state in the low two bits, such a code will have the following
 * identities for even 'n' < 64:
 *
 * 	encode_state(n) = encode_state(n+65)
 *	encode_state(n+1) = encode_state(n+64) = (3 ^ encode_state(n))
 *
 * Any convolutional code you would actually want to use will have
 * these properties, so these assumptions aren't too limiting.
 *
 * Doing this as a macro lets the compiler evaluate at compile time the
 * many expressions that depend on the loop index and encoder state and
 * emit them as immediate arguments.
 * This makes an enormous difference on register-starved machines such
 * as the Intel x86 family where evaluating these expressions at runtime
 * would spill over into memory.
 *
 * Two versions of the butterfly are defined. The first reads cmetric[]
 * and writes nmetric[], while the other does the reverse. This allows the
 * main decoding loop to be unrolled to two bits per loop, avoiding the
 * need to reference the metrics through pointers that are swapped at the
 * end of each bit. This was another performance win on the register-starved
 * Intel CPU architecture.
 */

#define	BUTTERFLY(i,sym) { \
	int m0,m1;\
	/* ACS for 0 branch */\
	m0 = cmetric[i] + mets[sym];		/* 2*i */\
	m1 = cmetric[i+32] + mets[7^sym];	/* 2*i + 64 */\
	nmetric[2*i] = m0;\
	if(m1 > m0){\
		nmetric[2*i] = m1;\
		dec |= 1 << ((2*i) & 31);\
	}\
	/* ACS for 1 branch */\
	m0 -= (mets[sym] - mets[7^sym]);\
	m1 += (mets[sym] - mets[7^sym]);\
	nmetric[2*i+1] = m0;\
	if(m1 > m0){\
		nmetric[2*i+1] = m1;\
		dec |= 1 << ((2*i+1) & 31);\
	}\
}
#define	BUTTERFLY2(i,sym) { \
	int m0,m1;\
	/* ACS for 0 branch */\
	m0 = nmetric[i] + mets[sym];	/* 2*i */\
	m1 = nmetric[i+32] + mets[7^sym]; /* 2*i + 64 */\
	cmetric[2*i] = m0;\
	if(m1 > m0){\
		cmetric[2*i] = m1;\
		dec |= 1 << ((2*i) & 31);\
	}\
	/* ACS for 1 branch */\
	m0 -= (mets[sym] - mets[7^sym]);\
	m1 += (mets[sym] - mets[7^sym]);\
	cmetric[2*i+1] = m0;\
	if(m1 > m0){\
		cmetric[2*i+1] = m1;\
		dec |= 1 << ((2*i+1) & 31);\
	}\
}


// implements the viterbi decoding algorithm for Rate 1/3 K=7
void compute(struct Input *input, struct Output *output) {

	unsigned char *symbols = input->symbols;

	int startstate=0, endstate=0;

	int bitcnt = -6; 		/* K-1 */
	int i,mets[8];
	unsigned long dec,paths[(NBITS+6)*2],*pp;
	long cmetric[64],nmetric[64];

	startstate &= 63;
	endstate &= 63;

	/* Initialize starting metrics */
	for(i=0;i<64;i++)
		cmetric[i] = -999999;
	cmetric[startstate] = 0;

	pp = paths;
	// picking a reasonable number of loops to guarantee decoding
	for(int loops=0; loops<1158; loops++) {

		/* Read input symbol triplet and compute branch metrics */
		mets[0] = table[0][symbols[0]] + table[0][symbols[1]] + table[0][symbols[2]];
		mets[1] = table[0][symbols[0]] + table[0][symbols[1]] + table[1][symbols[2]];
		mets[3] = table[0][symbols[0]] + table[1][symbols[1]] + table[1][symbols[2]];
		mets[2] = table[0][symbols[0]] + table[1][symbols[1]] + table[0][symbols[2]];
		mets[6] = table[1][symbols[0]] + table[1][symbols[1]] + table[0][symbols[2]];
		mets[7] = table[1][symbols[0]] + table[1][symbols[1]] + table[1][symbols[2]];
		mets[5] = table[1][symbols[0]] + table[0][symbols[1]] + table[1][symbols[2]];
		mets[4] = table[1][symbols[0]] + table[0][symbols[1]] + table[0][symbols[2]];
		symbols += 3;

		dec = 0;
		BUTTERFLY(0,0);
		BUTTERFLY(14,0);
		BUTTERFLY(2,7);
		BUTTERFLY(12,7);
		BUTTERFLY(1,6);
		BUTTERFLY(15,6);
		BUTTERFLY(3,1);
		BUTTERFLY(13,1);
		BUTTERFLY(4,5);
		BUTTERFLY(10,5);
		BUTTERFLY(6,2);
		BUTTERFLY(8,2);
		BUTTERFLY(5,3);
		BUTTERFLY(11,3);
		BUTTERFLY(7,4);
		BUTTERFLY(9,4);
		*pp++ = dec;
		dec = 0;

		BUTTERFLY(19,0);
		BUTTERFLY(29,0);
		BUTTERFLY(17,7);
		BUTTERFLY(31,7);
		BUTTERFLY(18,6);
		BUTTERFLY(28,6);
		BUTTERFLY(16,1);
		BUTTERFLY(30,1);
		BUTTERFLY(23,5);
		BUTTERFLY(25,5);
		BUTTERFLY(21,2);
		BUTTERFLY(27,2);
		BUTTERFLY(22,3);
		BUTTERFLY(24,3);
		BUTTERFLY(20,4);
		BUTTERFLY(26,4);
		*pp++ = dec;

		if(++bitcnt == NBITS){
			output->metric = nmetric[endstate];
			break;
		}

	}

	/* Chain back from terminal state to produce decoded data */
	for(i=0; i<NBYTES; i++) { output->data[i] = 0; }

	for(i=NBITS-1;i >= 0;i--){
		pp -= 2;
		if(pp[endstate/32] & (1 << (endstate & 31))){
			endstate |= 64;					/* 2^(K-1) */
			output->data[i/8] |= 0x80 >> (i&7);
		}
		endstate >>= 1;
	}
	return;
}
