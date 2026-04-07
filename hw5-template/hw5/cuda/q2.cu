
#include <__clang_cuda_runtime_wrapper.h>
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>




int main(int argc, char **argv)
{
    // Implement your solution for question 2. The input file is inp.txt
    // and contains an array A.
    // Running this program should output two files:
    //  (1) q2a.txt which contains the minimum value in the input array
    //  (2) q2b.txt which contains an array B (in the same format as inp.txt)
    //      where B[i] = the last digit of A[i]


    // read from inp.txt
    FILE *file = fopen("inp.txt","r");
    if(!file){
        printf("Failed to Open file");
    }

    // Count integers
    int n = 0, val;
    char sep;
    while (fscanf(file, "%d%c", &val, &sep) == 2) n++;
    rewind(file);
    int *h_A = (int*)malloc(n * sizeof(int));
    for (int i = 0; i < n; i++) fscanf(file, "%d%c", &h_A[i], &sep);
    fclose(file);



    return 0;
}
