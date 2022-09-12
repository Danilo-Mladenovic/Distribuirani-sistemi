#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>

int main(int argc, char** argv)
{
    int size, rank, root = 0;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int num_of_steps = log2(size), info[size];

    if (rank == root)
        for (int i = 0; i < size; i++)
            info[i] = i;

    for (int i = 1 << (num_of_steps - 1); i > 0; i >>= 1)
    {
        
    }

    MPI_Finalize();
    return 0;
}