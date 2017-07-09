    /* C program to solve the two-dimensional Ising model.
              "Maybe it works, maybe it doesn't".           */

#include <stdio.h>
#include <math.h>
#define LENGTH 30          /* system size is LENGTH*LENGTH */
#define TEMP   0.9         /* TEMP in units of interaction and k_B */
#define WARM   5
#define MCS     100

   /* Subroutines for initial conditions, configuration printer,
    monte carlo moves, random number generator, and so on, below.  */

void   initialize( int [][LENGTH], int [] , int []);
int    total_energy( int [][LENGTH], int [] , int []);
int    total_mag( int [][LENGTH]);
void   mcmove( int[][LENGTH] , int [] , int [] );
void   print_config ( int [][LENGTH] , int );
void   print_config_alt( int [][LENGTH] , int );
double ran3( long int *);
float  fpow (float , float);
float  mag_analytic(float);

main()
{
   int itime;
   int i;
   long int iseed;
   int spin[LENGTH][LENGTH];
   int nbr1[LENGTH];
   int nbr2[LENGTH];
   int big_energy;
   int big_mag;
   double E_per_spin;
   double M_per_spin;

     iseed= -12888333;
     itime = 0;
     big_energy = 0;
     big_mag = 0;

/* get started */
   initialize(spin, nbr1, nbr2);

/* warm up system */
     for (i = 1 ; i <= WARM; i++) 
     { 
	     itime = i;
	     mcmove(spin, nbr1, nbr2);
     }

/* do Monte Carlo steps and collect stuff for averaging */
     for (i = (WARM + 1) ; i <= MCS; i++) 
     { 
  	itime = i;
	mcmove(spin, nbr1, nbr2);
	   big_mag = big_mag + total_mag(spin);
	   big_energy = big_energy + total_energy(spin,nbr1,nbr2);
     }
        M_per_spin = big_mag;
        M_per_spin = M_per_spin/((MCS - WARM)*(LENGTH*LENGTH));
        E_per_spin = big_energy;
        E_per_spin = E_per_spin/((MCS - WARM)*(LENGTH*LENGTH));

/* finish off */
   printf("Mag/spin %lf \n", M_per_spin);
   printf("Energy/spin %lf \n", E_per_spin);
   printf("Analytic: Mag/spin %f, Energy/spin -2 to 0\n", mag_analytic(TEMP));
   printf("Temperature %lf, Edge length of system %d \n", TEMP , LENGTH);
   printf("No. of warm-up steps %d, No. of MCS %d \n", WARM , MCS);
}

void initialize( int spin[][LENGTH], int nbr1[], int nbr2[])
{
 int i, ix, iy;
     for (ix = 0 ; ix < LENGTH; ix++) /* start magnetized all spins = 1 */
     for (iy = 0 ; iy < LENGTH; iy++)
	       spin[ix][iy] = 1;

     for (i = 0 ; i < LENGTH ; i++)     /* periodic boundary conditions */
     {
       nbr1[i] = i - 1;
       nbr2[i] = i + 1;
       if (i == 0 )         nbr1[i] = LENGTH - 1;
       if (i == LENGTH - 1) nbr2[i] = 0;
      }
}

int total_mag( int spin[][LENGTH] )        /* total magnetization */
{
	int i, ix, iy;
	int foom;
        foom = 0;
             for (ix = 0 ; ix < LENGTH; ix++) 
             for (iy = 0 ; iy < LENGTH; iy++) 
 	     foom = foom + spin[ix][iy];
 	return(foom);
}

int total_energy( int spin[][LENGTH] , int nbr1[] , int nbr2[])
{                                             /* total energy */
	int ix, iy;
	int fooe;
	fooe = 0;
                 for (ix = 0 ; ix < LENGTH; ix++) 
                 for (iy = 0 ; iy < LENGTH; iy++) 
		 fooe = fooe - spin[ix][iy] * spin[ix][nbr1[iy]]
		             - spin[ix][iy] * spin[nbr2[ix]][iy];
	 return(fooe);
}

void mcmove( int spin[][LENGTH], int nbr1[] , int nbr2[])
{

/* ONE MONTE CARLO STEP by Metropolis: Flip probability 1 if Enew < Eold, 
   else prob is exp -(Enew-Eold)/T.  Simplified here since only there 
   are five cases in d=2 for external field = 0.
   FLIP WITH prob1   prob2    1.0     1.0     1.0   (Below spins called)
               +       -       -       -       -           ss2
             + + +   + + +   + + -   + + -   - + -      ss1 ss0 ss3
               +       +       +       -       -           ss4          */

  int i, ix, iy;
  int ixpick, iypick;
  int ss0, ss1, ss2, ss3, ss4, de;
  int flag;
  long int idum;
  double prob1 , prob2;
  prob1 = exp(-8.0/TEMP);
  prob2 = exp(-4.0/TEMP);
  for (i = 1 ; i <= LENGTH*LENGTH ; i++)
  {
	  ixpick = LENGTH * ran3(&idum) ;	  
	  iypick = LENGTH * ran3(&idum) ;	  

      ss0 = spin [ixpick]       [iypick]       ;     
      ss1 = spin [nbr1[ixpick]] [iypick]       ;
      ss2 = spin [ixpick]       [nbr1[iypick]] ;
      ss3 = spin [nbr2[ixpick]] [iypick]       ;
      ss4 = spin [ixpick]       [nbr2[iypick]] ;

      de =  2*ss0*(ss1+ss2+ss3+ss4);

      flag = 1;                     /* flip spin if flag = 1 */

             if ( (de == 8) && (ran3(&idum) > prob1)
			||    
         	  (de == 4) && (ran3(&idum) > prob2) )     
	     flag = 0;
	 
       spin[ixpick][iypick] = (1 - 2*flag )*spin[ixpick][iypick];
  }
}


void print_config( int spin[][LENGTH] , int itime )
{                             /* print concentrations "+"-> 1, "-"-> 0 */
   int ix , iy;
          for (ix = 0 ; ix < LENGTH; ix++) 
          { 
            for (iy = 0 ; iy < LENGTH; iy++) 
            { 
		    printf("%d", (spin[ix][iy]+1)/2 ); 
	    }
          printf("\n"); 
          }
         printf(" %d \n", itime); 
}

void print_config_alt( int spin[][LENGTH] , int itime )
{                                                       /* print spins */
   int ix, iy;
   printf("Configuration at time %d \n", itime); 
         for (ix = 0 ; ix < LENGTH; ix++) 
         { 
	   for(iy = 0 ; iy < LENGTH; iy++)
	   { 
         printf("%d",spin[ix][iy]); 
	   }
         printf("\n"); 
         }
         printf("\n"); 
}

#define Tc 2.2691853142130216092 

float mag_analytic(float temp)     /* analytic solution for magnetization */
{
 int i;
 float mag;
   if (temp >= Tc) mag = 0.0;
   else            mag = fpow( (1.0-pow(sinh(2.0/temp),-4)) , 0.125);
 return(mag);
}

float fpow ( float x , float power )     /* raises to a noninteger power */
{
 float thing;
 thing = exp ( power * log ( x ) );
 return(thing);
}

#include <stdlib.h>         
#define MBIG 1000000000
#define MSEED 161803398              /* portable random number generator */
#define MZ 0                         /* from numerical recipes book */
#define FAC (1.0/MBIG)
 
double ran3(long *idum)
{
  static int inext,inextp;
  static long ma[56];
  static int iff=0;
  long mj,mk;
  int i,ii,k;
  
  if (*idum < 0 || iff == 0){
      iff=1;
      mj = labs(MSEED-labs(*idum));
      mj %= MBIG;
      ma[55]=mj;
      mk=1;
      for (i=1;i<=54;i++){
          ii=(21*i) % 55;
          ma[ii]=mk;
          mk=mj-mk;
          if (mk < MZ) mk += MBIG;
          mj =ma[ii];
      }
      for (k=1;k<=4;k++)
          for (i=1;i<=55;i++) {
              ma[i] -= ma[1+(i+30) % 55];
              if (ma[i] < MZ) ma[i] += MBIG;
          }
      inext=0;
      inext=31;
      *idum=1;
  }
  if (++inext == 56) inext=1; 
  if (++inextp == 56) inextp=1; 
  mj =ma[inext]-ma[inextp];
  if (mj < MZ) mj += MBIG;
  ma[inext] =mj;
  return mj*FAC;
}
