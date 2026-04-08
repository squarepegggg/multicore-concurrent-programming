#include <cuda_runtime.h>
#include <stdio.h>
#include <stdlib.h>

#define CUDA_CHECK(call) do { \
    cudaError_t err = (call); \
    if (err != cudaSuccess) { \
        fprintf(stderr, "CUDA error at %s:%d: %s\n", __FILE__, __LINE__, \
                cudaGetErrorString(err)); \
        exit(EXIT_FAILURE); \
    } \
} while(0)

// Q3a: each thread increments global B directly
__global__ void histogramGlobal(int *d_A, int *d_B, int n)
{
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n)
        atomicAdd(&d_B[d_A[idx] / 100], 1);
}

// Q3b: each block uses shared memory local copy, then merges into global
__global__ void histogramShared(int *d_A, int *d_B, int n)
{
    __shared__ int localB[10];

    // Initialize shared memory
    if (threadIdx.x < 10)
        localB[threadIdx.x] = 0;
    __syncthreads();

    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n)
        atomicAdd(&localB[d_A[idx] / 100], 1);
    __syncthreads();

    // Merge local copy into global
    if (threadIdx.x < 10)
        atomicAdd(&d_B[threadIdx.x], localB[threadIdx.x]);
}

int main(int argc, char **argv)
{
    // ── 1. Read inp.txt ──────────────────────────────────────────────────────
    FILE *file = fopen("inp.txt", "r");
    if (!file) { fprintf(stderr, "Failed to open inp.txt\n"); return 1; }

    int n = 1, c;
    while ((c = fgetc(file)) != EOF)
        if (c == ',') n++;
    rewind(file);

    int *h_A = (int*)malloc(n * sizeof(int));
    for (int i = 0; i < n; i++) {
        fscanf(file, " %d", &h_A[i]);
        if (i < n - 1) fscanf(file, " ,");
    }
    fclose(file);

    // ── 2. Device setup ──────────────────────────────────────────────────────
    int *d_A, *d_B;
    CUDA_CHECK(cudaMalloc(&d_A, n * sizeof(int)));
    CUDA_CHECK(cudaMalloc(&d_B, 10 * sizeof(int)));
    CUDA_CHECK(cudaMemcpy(d_A, h_A, n * sizeof(int), cudaMemcpyHostToDevice));

    int blockSize = 256;
    int gridSize  = (n + blockSize - 1) / blockSize;

    int h_B[10];

    // ── 3a. Global memory histogram ──────────────────────────────────────────
    CUDA_CHECK(cudaMemset(d_B, 0, 10 * sizeof(int)));
    histogramGlobal<<<gridSize, blockSize>>>(d_A, d_B, n);
    CUDA_CHECK(cudaGetLastError());
    CUDA_CHECK(cudaDeviceSynchronize());
    CUDA_CHECK(cudaMemcpy(h_B, d_B, 10 * sizeof(int), cudaMemcpyDeviceToHost));

    FILE *fa = fopen("q3a.txt", "w");
    for (int i = 0; i < 10; i++) {
        if (i > 0) fprintf(fa, ", ");
        fprintf(fa, "%d", h_B[i]);
    }
    fprintf(fa, "\n");
    fclose(fa);

    // ── 3b. Shared memory histogram ──────────────────────────────────────────
    CUDA_CHECK(cudaMemset(d_B, 0, 10 * sizeof(int)));
    histogramShared<<<gridSize, blockSize>>>(d_A, d_B, n);
    CUDA_CHECK(cudaGetLastError());
    CUDA_CHECK(cudaDeviceSynchronize());
    CUDA_CHECK(cudaMemcpy(h_B, d_B, 10 * sizeof(int), cudaMemcpyDeviceToHost));

    FILE *fb = fopen("q3b.txt", "w");
    for (int i = 0; i < 10; i++) {
        if (i > 0) fprintf(fb, ", ");
        fprintf(fb, "%d", h_B[i]);
    }
    fprintf(fb, "\n");
    fclose(fb);

    // ── 3c. Prefix sum of B (CPU, using only B not A) ────────────────────────
    // h_B already has the counts from q3b; compute cumulative sum
    int h_C[10];
    h_C[0] = h_B[0];
    for (int i = 1; i < 10; i++)
        h_C[i] = h_C[i-1] + h_B[i];

    FILE *fc = fopen("q3c.txt", "w");
    for (int i = 0; i < 10; i++) {
        if (i > 0) fprintf(fc, ", ");
        fprintf(fc, "%d", h_C[i]);
    }
    fprintf(fc, "\n");
    fclose(fc);

    printf("q3a.txt, q3b.txt, q3c.txt written.\n");

    // ── 4. Cleanup ───────────────────────────────────────────────────────────
    free(h_A);
    cudaFree(d_A);
    cudaFree(d_B);

    return 0;
}