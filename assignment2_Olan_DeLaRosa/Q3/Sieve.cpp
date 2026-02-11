#include <stdlib.h>
#include <stdio.h>
#include <omp.h>
#include <ctime>
#include <string>
#include <iostream>
#include <fstream>
#include <iomanip>
#include <math.h>
int Sieve(int N, int threads){
    if (N < 2) return 0;
    char *isPrime = (char *)calloc(N + 1, sizeof(char));
    isPrime[0] = isPrime[1] = 1;
    omp_set_num_threads(threads);
    int sqrtN = (int)sqrt(N);

    for (int p = 2; p <= sqrtN; p++) {
        if (isPrime[p] == 0) {
            #pragma omp parallel for
            for (int i = p * p; i <= N; i += p) {
                isPrime[i] = 1;
            }
        }
    }

    int count = 0;
    #pragma omp parallel for reduction(+:count)
    for (int i = 2; i <= N; i++) {
        if (isPrime[i] == 0) {
            count++;
        }
    }

    free(isPrime);
    return count;
}


int main(){
    int N = 100000000;
    int threads = 8;
    double start = omp_get_wtime();
    int count = Sieve(N,threads);
    double end = omp_get_wtime();
    printf("%d\n",count);
    printf("%f\n", end - start);
}