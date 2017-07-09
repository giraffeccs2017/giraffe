/* C program to solve the one-dimensional Ising model 
    "Maybe it works.maybe it doesn't." */

// Test compile this with gcc -c ising1-simple.c 

#define LENGTH 1000
#define TEMP   2.0  /* in units of interaction and k_B */
#define WARM   10
#define MCS    1000


struct Input {
	int a;
  	unsigned long long seed;
};
struct Output {
	long E_per_spin;
  	long M_per_spin;
};


// parameters for RNG
// using a Linear Congruential generator
// x_{n+1} = 616318177 * x_n + 524287 mod 2305843009213693951
// based on Knuth theorem.  a,c,m are all prime

unsigned long long a = 616318177; //prime
unsigned long long m = 2305843009213693951; // 2^61 -1 
unsigned long long c = 524287; //prime
unsigned long long x = 1;

#define ranstep() {				\
	x = ( a*x + c ) % m;		\
}								

/* Picks a random value in the range of 1...LENGTH and sets it to var
   assumes that x,a,c,m are variables that are in the scope
*/
#define rand1(var) 				\
{								\
	ranstep();					\
	var = (x/2305843009213694);	\
}								

/* Flips a coin with pr  0.135335283236613 prob is //exp (-4.0/TEMP)
	if value is less than 312061916751099310 ...njuikolp['']
*/
#define rand2(var) 				\
{								\
	ranstep();					\
	if (x<312061916751099310) {	\
		var = 1;				\
	} else {					\
		var = 0;				\
	}							\
}								

 /* 
ONE MONTE CARLO STEP by Metropolis: Flip probability 1 if Enew < Eold,
else prob is exp -(Enew-Eold)/T.  Simplified here since there only
are three cases in d=1: +++ ++- -+-.  Only need to flip with
nontrivial probability when all equal (+++ or --- equivalently).
 */

// prob is //exp (-4.0/TEMP);
#define mcmove( spin, nbr1, nbr2 ) \
{															\
	int ipick, flip;										\
	for (int i = 0 ; i < LENGTH ; i++) {					\
		rand2(flip);										\
		rand1(ipick); 	  									\
		if ( (spin[ipick] == spin[nbr1[ipick]]) 			\
			&&												\
			(spin[nbr1[ipick]] == spin[nbr2[ipick]]) 		\
			&&												\
			(flip)											\
		) {   												\
			;												\
		}													\
		else { 												\
			spin[ipick] = -1*spin[ipick];					\
		}          											\
	}														\
}															



void compute(struct Input *input, struct Output *output) {

	int itime;
	int i;
	long int iseed;
	int spin[LENGTH];
	int nbr1[LENGTH];
	int nbr2[LENGTH];
	int big_energy;
	int big_mag;
   
	x = input->seed;
	

	big_energy = 0;
	big_mag = 0;
	itime = 0;
	// double energy_analytic;
	// energy_analytic = -1.0 * 0.46211715726001; // tanh (1.0/TEMP);

	/* start magnetized all spins = 1 */
	/* periodic boundary conditions */
	for (i = 0 ; i < LENGTH; i++) {
		spin[i] = 1;
		nbr1[i] = i - 1;
		nbr2[i] = i + 1;
		if (nbr1[i] == -1 ) nbr1[i] = LENGTH;
		if (nbr2[i] == LENGTH) nbr2[i] = 0;
	}


	/* warm up system */
	for (i = 1 ; i <= WARM; i++) { 
		itime = i;
		mcmove(spin, nbr1, nbr2);
	}

	/* do Monte Carlo steps */
	for (i = (WARM + 1) ; i <= MCS; i++) { 
		itime = i;
		mcmove(spin, nbr1, nbr2);

		int foom = 0;
		for (int j = 0 ; j < LENGTH; j++) foom = foom + spin[j];

		int fooe = 0;
		for (i = 0;i<LENGTH;i++) fooe=fooe-spin[i]*spin [nbr2[i]];

		big_mag = big_mag + foom;
		big_energy = big_energy + fooe;
	}

	output->M_per_spin = big_mag;	///((MCS - WARM)*(LENGTH));
	output->E_per_spin = big_energy; 	///((MCS - WARM)*(LENGTH));


	return;
}


// int main() {
// 	struct Input I;
// 	struct Output O;
// 	I.seed = 1231222;
// 	compute(&I,&O);
// 	printf("%ld %ld\n",O.E_per_spin,O.M_per_spin);
// }

