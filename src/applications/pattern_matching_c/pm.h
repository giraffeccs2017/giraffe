
/* 
need to first define constants:

TSIZE
NUMCONV
PSIZE
CONVSIZE
LOGN
COPIES

also need: 
    int s;
    int c, q;
    int tmp;
    int p2fft[CONVSIZE]; // p^2 padded with 0
    int pfft[CONVSIZE]; // t padded with 0
    int p3tmp[PSIZE]; // p^3
    int t2fft[NUMCONV][CONVSIZE]; // t^2 padded with 0
    int tfft[NUMCONV][CONVSIZE]; // t padded with 0

    to be declared in the scope
 */

/*
 * The compute function is doing pattern matching with don't-cares
 * in the pattern, i.e., computing:
 * m_j = \sum p_i * (t_{i+j} - p_i)^2 
 * = \sum p_i^3 + p_i * t_{j+i}^2 + 2 * p_i^2 * t_{j+i}
 * which is broken down into
 * 1) prerpocessing:
 *  t'_i = t_i * t_i,
 *  p'_i = 2 * p_i * p_i
 *
 * 2) ntt(p), ntt(t), ntt(p'), ntt(t')
 *
 * 3) multiply corresponding terms
 * 
 * 4) 2 inverse-ntts to get the convolution
 *
 * 5) doing necessary sums to get the m_j
 */

/*
 * FFT/IFFT are taken from CLRS Chapter 30.'s iterative version
 *
 * where 
 * FFT =  bit_reversal . butterfly'
 * IFFT = butterfly . bit_reversal
 *
 * So when composing the two, we can save the bit_reversal step
 * since bit_rev = bit_rev^{-1}
 */

#define fft(om, x) \
    for (s = LOGN; s >= 1; s--) { \
        int k;               \
        int i, j;            \
        int m;               \
        m = 1;                \
        for (i = 0; i < s; i++) { \
            m = m * 2;          \
        }                      \
        int mm = m / 2;        \
        for (k = 0; k <= CONVSIZE-1; k = k + m) { \
            for (j = 0; j <= mm - 1; j++) {       \
                int t = x[k+j] + x[ k + j + mm]; \
                int u = x[k+j] - x[ k + j + mm]; \
                x[ k+j] = t;   \
                x[ k + j + mm] = u * om[j]; \
            } \
        } \
    }


#define ifft(om, x)  \
    for (s = 1; s <= LOGN; s++) { \
        int k;         \
        int i, j; \
        int m; \
        m = 1; \
        for (i = 0; i < s; i++) {\
            m = m * 2; \
        } \
        int mm = m / 2;\
        for (k = 0; k <= CONVSIZE-1; k = k + m) {\
            for (j = 0; j <= mm - 1; j++) { \
                int t = om[j] * x[k + j + mm];\
                x[k + j + mm] = x[k + j] - t;\
                x[ k+j] = x[k + j]+ t;\
            } \
        }\
    }


#define pm(patt, text, matches)   \
    for (c = 0; c < COPIES; c++) { \
        int omegaConcrete[CONVSIZE];  \
        int omegaIConcrete[CONVSIZE]; \
        int i; \
        omegaConcrete[0] = 1; \
        omegaIConcrete[0] = 1; \
        for (i = 1; i < LOGN; i++) { \
            int m, j, k; \
            m = 1; \
            for (j = 0; j < i; j++) { \
                m = m * 2; \
            } \
            for (k = m / 2; k < m; k++) { \
                omegaConcrete[k] = omegaConcrete[k - m / 2] * input->omegaP[i]; \
                omegaIConcrete[CONVSIZE - k] = omegaConcrete[k]; \
            } \
        } \
        /* preparing pattern */ \
        for (s = 0; s < PSIZE; s++) { \
            /* invert pattern */ \
            p2fft[s] = patt[c][PSIZE-s-1] * patt[c][PSIZE-s-1]; \
            pfft[s] = patt[c][PSIZE-s-1]; \
            p3tmp[s] = p2fft[s] * patt[c][PSIZE-s-1]; \
            p2fft[s] = 0 - p2fft[s] * 2; \
            /* padding */ \
            p2fft[s+PSIZE] = 0; \
            pfft[s+PSIZE] = 0; \
        } \
        /* adder tree to compute \sum p^3 */ \
        for (s = PSIZE/2; s >= 1; s = s / 2) { \
            for (tmp = 0; tmp < s; tmp++) { \
                p3tmp[tmp] = p3tmp[tmp] + p3tmp[tmp+s]; \
            } \
        } \
        /* FFT of p^2 */ \
        fft(omegaConcrete, p2fft); \
        /* FFT of p */ \
        fft(omegaConcrete, pfft); \
 \
        /* preparing text */ \
        for (s = 0; s < NUMCONV; s++) { \
            for (q = 0; q < PSIZE; q++) { \
                tfft[s][q] = text[c][s*PSIZE+q]; \
                t2fft[s][q] = text[c][s*PSIZE+q]*text[c][s*PSIZE+q]; \
                t2fft[s][q+PSIZE] = 0; \
                tfft[s][q+PSIZE] = 0; \
            } \
        } \
            \
        for (tmp = 0; tmp < NUMCONV; tmp++) { \
            /* FFT of t^2 */ \
            fft(omegaConcrete, t2fft[tmp]); \
            /* FFT of t */ \
            fft(omegaConcrete, tfft[tmp]); \
            /* multiply the relavant terms in freq. domain */ \
            for (s = 0; s < CONVSIZE; s++) { \
                tfft[tmp][s] = tfft[tmp][s] * p2fft[s]; \
            } \
            for (s = 0; s < CONVSIZE; s++) { \
                t2fft[tmp][s] = t2fft[tmp][s] * pfft[s]; \
            } \
            /* IFFT */ \
            ifft(omegaIConcrete, tfft[tmp]); \
            ifft(omegaIConcrete, t2fft[tmp]); \
            /* add terms together, invn is convsize^{-1} */ \
            for (s = 0; s < CONVSIZE; s++) { \
                tfft[tmp][s] = p3tmp[0] + (t2fft[tmp][s]+tfft[tmp][s]) * input->invn; \
            } \
        } \
        matches[c][0] = tfft[0][PSIZE-1]; \
        i = 1; \
        /* finaly add them together to get the result of the convolution */ \
        for (tmp = 1; tmp < NUMCONV; tmp++) { \
            for (s = 0; s < PSIZE; s++) { \
                matches[c][i] = tfft[tmp][s] + tfft[tmp-1][s+PSIZE]; \
                i = i + 1; \
            } \
        } \
    }

