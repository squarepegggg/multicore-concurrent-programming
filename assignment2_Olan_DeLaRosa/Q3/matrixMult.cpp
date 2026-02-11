#include <stdlib.h>
#include <stdio.h>
#include <omp.h>
#include <ctime>
#include <string>
#include <iostream>
#include <fstream>
#include <iomanip>
using namespace std;

void randomizeFile(const char* filename);

void populateFile(double array[][100]){
    FILE *currFile = fopen("matrixC.txt","w");
    if(currFile == NULL){
        perror("Error opening file");
        return;
    }
    for(int i = 0; i < 100; i++){
        for(int j = 0; j < 100; j++){
            fprintf(currFile, "%.2f ", array[i][j]);
        }
        fprintf(currFile,"\n");
    }
    fclose(currFile);
    printf("Matrix C Success\n");
}
void dotProduct(double arrayA[][100], double arrayB[][100],double arrayC[][100], int Threads){
    omp_set_num_threads(Threads);
    #pragma omp parallel for
    for(int i = 0; i < 100; i++){
        for(int k = 0; k < 100; k++){
            double temp = arrayA[i][k];
            for(int j = 0; j < 100; j++){
                arrayC[i][j] += temp * arrayB[k][j];
            }
        }
    }        

}

void populateArray(const char* fileName, double array[][100]){
    FILE *currFile = fopen(fileName,"r");
    if(currFile == NULL){
        perror("Error opening file");
        return;
    }
    for(int i = 0; i < 100; i++){
        for(int j = 0; j < 100; j++){
            if(fscanf(currFile,"%lf",&array[i][j]) != 1){
                fprintf(stderr, "Error reading data at [%d][%d]\n",i,j);
                fclose(currFile);
                return;
            }
        }
    }
    fclose(currFile);
    printf("Array '%s' populated.\n",fileName);
}


//
// to run program do "./matrixMult <file1> <file2> (# of threads)"

int main(int argc, char *argv[]){

    double matrixA [100][100] = {0};
    double matrixB [100][100] = {0};
    double matrixC [100][100] = {0};

    const char* matrixTXTA = argv[1];
    const char* matrixTXTB = argv[2];
    const int threads = atoi(argv[3]);
    // uncomment this if you don't want to write your own test cases
    //randomizeFile(matrixTXTA);
    //randomizeFile(matrixTXTB);
    printf("%s\n",matrixTXTA);
    printf("%s\n",matrixTXTB);
    printf("%d\n",threads);
    

    
    populateArray(matrixTXTA, matrixA);
    populateArray(matrixTXTB, matrixB);

    double start = omp_get_wtime();
    dotProduct(matrixA,matrixB,matrixC,threads);
    double end = omp_get_wtime();
    populateFile(matrixC);
    printf("%f\n", end - start);
    

    return 0;
}


// For test cases


void randomizeFile(const char* fileName){
    FILE* curFile = fopen(fileName,"w");
    if(curFile == NULL){
        printf("File is Empty\n");
        return;
    }

    for(int i = 0; i < 100; i++){
        for(int j = 0; j < 100; j++){
            double randomValue = (double)(rand()) / RAND_MAX * 10.0;
            fprintf(curFile,"%.2f ", randomValue);
        }
        fprintf(curFile,"\n");
    }
    fclose(curFile);
    printf(".txtfile '%s' randomized.\n",fileName);

}
