#include <iostream>
#include <fstream>
#include <vector>
#include <string>
#include <cuda_runtime.h>

// Kernel: mark odd numbers with 1, even with 0
__global__ void markOdd(int *A, int *flags, int n) {
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n) {
        flags[idx] = (A[idx] % 2 != 0) ? 1 : 0;
    }
}

// Kernel: scatter odd values into output using prefix sum positions
__global__ void scatter(int *A, int *flags, int *prefixSum, int *D, int n) {
    int idx = blockIdx.x * blockDim.x + threadIdx.x;
    if (idx < n && flags[idx] == 1) {
        D[prefixSum[idx] - 1] = A[idx];
    }
}

// Host-side prefix sum (exclusive scan -> inclusive)
void inclusivePrefixSum(int *flags, int *prefixSum, int n) {
    prefixSum[0] = flags[0];
    for (int i = 1; i < n; i++) {
        prefixSum[i] = prefixSum[i - 1] + flags[i];
    }
}

int main(int argc, char **argv) {
    // --- Read input ---
    std::ifstream infile("inp.txt");
    std::vector<int> A;
    std::string token;
    while (std::getline(infile, token, ',')) {
        size_t start = token.find_first_not_of(" \t\n\r");
        size_t end   = token.find_last_not_of(" \t\n\r");
        if (start != std::string::npos)
            A.push_back(std::stoi(token.substr(start, end - start + 1)));
    }
    infile.close();

    int n = (int)A.size();

    // --- Allocate host memory ---
    std::vector<int> h_flags(n), h_prefixSum(n);

    // --- Allocate device memory ---
    int *d_A, *d_flags, *d_prefixSum, *d_D;
    cudaMalloc(&d_A,         n * sizeof(int));
    cudaMalloc(&d_flags,     n * sizeof(int));
    cudaMalloc(&d_prefixSum, n * sizeof(int));

    // --- Copy input to device ---
    cudaMemcpy(d_A, A.data(), n * sizeof(int), cudaMemcpyHostToDevice);

    // --- Launch markOdd kernel ---
    int blockSize = 256;
    int gridSize  = (n + blockSize - 1) / blockSize;
    markOdd<<<gridSize, blockSize>>>(d_A, d_flags, n);
    cudaDeviceSynchronize();

    // --- Copy flags back, compute prefix sum on host ---
    cudaMemcpy(h_flags.data(), d_flags, n * sizeof(int), cudaMemcpyDeviceToHost);
    inclusivePrefixSum(h_flags.data(), h_prefixSum.data(), n);

    int outSize = h_prefixSum[n - 1];

    // --- Copy prefix sum back to device, allocate output ---
    cudaMalloc(&d_D,         outSize * sizeof(int));
    cudaMemcpy(d_prefixSum, h_prefixSum.data(), n * sizeof(int), cudaMemcpyHostToDevice);

    // --- Launch scatter kernel ---
    scatter<<<gridSize, blockSize>>>(d_A, d_flags, d_prefixSum, d_D, n);
    cudaDeviceSynchronize();

    // --- Copy result back to host ---
    std::vector<int> D(outSize);
    cudaMemcpy(D.data(), d_D, outSize * sizeof(int), cudaMemcpyDeviceToHost);

    // --- Write output ---
    std::ofstream outfile("q4.txt");
    for (int i = 0; i < outSize; i++) {
        if (i > 0) outfile << ", ";
        outfile << D[i];
    }
    outfile << std::endl;
    outfile.close();

    // --- Cleanup ---
    cudaFree(d_A);
    cudaFree(d_flags);
    cudaFree(d_prefixSum);
    cudaFree(d_D);

    return 0;
}