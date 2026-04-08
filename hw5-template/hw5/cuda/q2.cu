
#include <cuda_runtime.h>
#include <stdio.h>
#include <stdlib.h>
#include <limits.h>

// Macro for CUDA error checking
#define CUDA_CHECK(call) do { \
    cudaError_t err = (call); \
    if (err != cudaSuccess) { \
        fprintf(stderr, "CUDA error at %s:%d: %s\n", __FILE__, __LINE__, \
                cudaGetErrorString(err)); \
        exit(EXIT_FAILURE); \
    } \
} while(0)

// Kernel (a): each thread checks one element and atomically updates the global min
__global__ void minKernel(int *d_A, int *d_min, int n)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n)
        atomicMin(d_min, d_A[idx]);
}

// Kernel (b): B[i] = last digit of A[i]
__global__ void lastDigitKernel(int *d_A, int *d_B, int n)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n)
        d_B[idx] = d_A[idx] % 10;
}

int main(int argc, char **argv)
{
    // ── 1. Read from inp.txt ─────────────────────────────────────────────────
    FILE *file = fopen("inp.txt", "r");
    if (!file) {
        printf("Failed to open file\n");
        return 1;
    }

    // Count integers by counting commas + 1
    int n = 1, c;
    while ((c = fgetc(file)) != EOF)
        if (c == ',') n++;
    rewind(file);

    int *h_A = (int*)malloc(n * sizeof(int));
    if (!h_A) { fprintf(stderr, "malloc failed\n"); return 1; }

    // FIX: use a single fscanf format that handles "int," or "int" cleanly
    for (int i = 0; i < n; i++) {
        if (fscanf(file, " %d", &h_A[i]) != 1) {
            fprintf(stderr, "Failed to read element %d\n", i);
            return 1;
        }
        if (i < n - 1) {
            // consume the comma separator
            fscanf(file, " ,");
        }
    }
    fclose(file);

    printf("Read %d elements. First: %d, Last: %d\n", n, h_A[0], h_A[n-1]);

    // ── 2. Allocate and copy to device ───────────────────────────────────────
    int *d_A, *d_B, *d_min;
    CUDA_CHECK(cudaMalloc(&d_A,   n * sizeof(int)));
    CUDA_CHECK(cudaMalloc(&d_B,   n * sizeof(int)));
    CUDA_CHECK(cudaMalloc(&d_min, sizeof(int)));

    CUDA_CHECK(cudaMemcpy(d_A, h_A, n * sizeof(int), cudaMemcpyHostToDevice));

    // FIX: initialize d_min to INT_MAX AFTER d_A is copied
    int initMin = INT_MAX;
    CUDA_CHECK(cudaMemcpy(d_min, &initMin, sizeof(int), cudaMemcpyHostToDevice));

    // FIX: zero-initialize d_B so unwritten entries are 0, not garbage
    CUDA_CHECK(cudaMemset(d_B, 0, n * sizeof(int)));

    // ── 3. Launch kernels ────────────────────────────────────────────────────
    int blockSize = 256;
    int gridSize  = (n + blockSize - 1) / blockSize;

    printf("Launching with gridSize=%d, blockSize=%d, n=%d\n", gridSize, blockSize, n);

    minKernel      <<<gridSize, blockSize>>>(d_A, d_min, n);
    CUDA_CHECK(cudaGetLastError());

    lastDigitKernel<<<gridSize, blockSize>>>(d_A, d_B, n);
    CUDA_CHECK(cudaGetLastError());

    CUDA_CHECK(cudaDeviceSynchronize());

    // ── 4. Copy results back ─────────────────────────────────────────────────
    int  h_min;
    int *h_B = (int*)malloc(n * sizeof(int));
    if (!h_B) { fprintf(stderr, "malloc failed\n"); return 1; }

    CUDA_CHECK(cudaMemcpy(&h_min, d_min, sizeof(int), cudaMemcpyDeviceToHost));
    CUDA_CHECK(cudaMemcpy(h_B,    d_B,   n * sizeof(int), cudaMemcpyDeviceToHost));

    // ── 5. Write output files ────────────────────────────────────────────────
    FILE *fa = fopen("q2a.txt", "w");
    if (!fa) { fprintf(stderr, "Failed to open q2a.txt\n"); return 1; }
    fprintf(fa, "%d\n", h_min);
    fclose(fa);

    FILE *fb = fopen("q2b.txt", "w");
    if (!fb) { fprintf(stderr, "Failed to open q2b.txt\n"); return 1; }
    for (int i = 0; i < n; i++) {
        if (i > 0) fprintf(fb, ",");
        fprintf(fb, "%d", h_B[i]);
    }
    fprintf(fb, "\n");
    fclose(fb);

    printf("minA = %d  ->  q2a.txt\n", h_min);
    printf("Array B written  ->  q2b.txt\n");

    // ── 6. Cleanup ───────────────────────────────────────────────────────────
    free(h_A); free(h_B);
    cudaFree(d_A); cudaFree(d_B); cudaFree(d_min);

    return 0;
}