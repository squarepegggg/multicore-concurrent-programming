#include <stdlib.h>
#include <stdio.h>
#include <omp.h>
#include <ctime>
#include <string>
#include <iostream>
#include <fstream>
#include <iomanip>
using namespace std;

void populateFile(const char* filename);

void dotProduct(double arrayA[], double arrayB[], int Threads){
    omp_set_num_threads(Threads);
    #pragma omp parallel for num_threads(Threads)
    for(int i = 0; i < 100; i++){
        for(int j = 0; j < 100; j++){

        }
    }        

}

void populateArray(const char* fileName, int *array[]){
    FILE *currFile = fopen(fileName,"r");
    if((currFile == nullptr)){
        perror("Error opening file");
        return;
    }
}



int main(int argc, char *argv[]){

    double matrixA [100][100] = {0};
    double matrixB [100][100] = {0};
    
    const char* emptyMatrixA = argv[1];
    const char* emptyMatrixB = argv[2];
    const int threads = atoi(argv[3]);
    // initializing matrixA,matrixB, and threads
    populateFile(emptyMatrixA);
    populateFile(emptyMatrixB);
    printf("%s\n",emptyMatrixA);
    printf("%s\n",emptyMatrixB);
    printf("%d\n",threads);
    

    
    populateArray();


    // // dotProduct(matrixA,matrixB,threads);

    // fclose(matrixA);
    // fclose(matrixB);


    return 0;
}


// For test cases


void populateFile(const char* fileName){
    FILE* curFile = fopen(fileName,"w");
    if(fileName == nullptr){
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
}
