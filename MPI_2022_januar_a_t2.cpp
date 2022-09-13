#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>

#define N 10


bool isPrime(int number)
{
    if (number <= 1)
        return false;
    if (number == 2 || number == 3)
        return true;
    for (int i = 4; i <= sqrt(number); i++)
        if (number % i == 0)
            return false;
    return true;
}


int main(int argc, char** argv)
{
    int size, rank, root = 0;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int local_sum = 0, final_sum;

    struct {
        int counter;
        int rank;
    } in = { 0, rank }, out;

    for (int i = rank; i < N; i += size)
        for (int k = 0; k < size; k++)
            for (int j = k; j < N; j += size)
            {
                local_sum = local_sum + i + j;

                if (isPrime(i + j))
                    in.counter++;
            }

    MPI_Reduce(&in, &out, 1, MPI_2INT, MPI_MINLOCK, root, MPI_COMM_WORLD);
    MPI_Bcast(&out, 1, MPI_2INT, root, MPI_COMM_WORLD);

    MPI_Reduce(&local_sum, &final_sum, 1, MPI_INT, out.rank, MPI_COMM_WORLD);

    if (rank == out.rank)
        printf(final_sum);

    MPI_Finalize();
    return 0;
}