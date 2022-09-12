#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>
#include <time.h>

int main(int argc, char** argv)
{
    int size, rank;
    MPI_Status stat;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int step, num_of_steps = log2(size), mask = 0, value = 10;

    for (int i = 0; i < num_of_steps; i++)
    {
        step = 1 << i;
        mask |= step;

        if ((rank & mask) == rank)
        {
            if ((rank ^ step) > rank)
                MPI_Send(&value, 1, MPI_INT, rank ^ step, 0, MPI_COMM_WORLD);
            else
            {
                MPI_Recv(&value, 1, MPI_INT, rank ^ step, 0, MPI_COMM_WORLD, &stat);
                printf(value);
            }
        }
    }

    MPI_Finalize();
    return 0;
}