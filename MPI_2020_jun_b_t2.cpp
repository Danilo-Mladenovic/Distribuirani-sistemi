#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>
#include <math.h>

#define N 10

bool isPrime(int number)
{
    if (number <= 1)
        return false;
    if (number <= 3)
        return true;
    for (int i = 4; i <= sqrt(number); i++)
        if (number % i == 0)
            return false;

    return true;
}

int main(int argc, char** argv)
{
    int size, rank, root = 0;
    MPI_Status status;
    MPI_Request request;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int local_sum = 0, final_sum;
    struct {
        int primes;
        int rank;
    } int = { 0, rank }, out;

    for (int i = rank; i < N; i+=size)
        for (int k = 0; k < size; k++)
            for (int j = k; j < N; j+= size)
            {
                local_sum = local_sum + i + j;
                if (isPrime(i + j))
                    in.primes++;
            }
    
    // Pronalazi proces sa najmanjim brojem parnih brojeva (in.primes)i stavlja ga u root
    // MPI_Reduce(&in, &out, 1, MPI_2INT, MPI_MINLOCK, root, MPI_COMM_WORLD);
    MPI_Isend(&in, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &request);
    if (rank == root)
    {
        MPI_Recv(&out, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &status);
        
        for (int i = 1; i < size; i++)
        {
            MPI_Recv(&in, 1, MPI_2INT, i, 0, MPI_COMM_WORLD, &status);
            if (in.primes < out.primes)
                out = in;
        }
    }


    // Prosledjuje proces sa najmanjim brojem parnih brojeva iz root u sve ostale 
    // MPI_Bcast(&out, 1, MPI_2INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&out, 1, MPI_2INT, i, 0, MPI_COMM_WORLD, &request);
    MPI_Recv(&out, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &status);


    // Nalazi sumu svih lokalnih suma i stavlja je u proces sa najmanjim brojem parnih br
    // MPI_Reduce(&local_sum, &final_sum, 1, MPI_INT, MPI_SUM, out.rank, MPI_COMM_WORLD);
    MPI_Isend(&local_sum, 1, MPI_INT, out.rank, 0, MPI_COMM_WORLD, &request);
    if (rank == out.rank)
    {
        MPI_Recv(&final_sum, 1, MPI_INT, root, 0, MPI_COMM_WORLD, &status);
        
        for (int i = 1; i < size; i++)
        {
            MPI_Recv(&local_sum, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &status);
            final_sum += local_sum;
        }
    }

    if (rank == out.rank)
        printf(final_sum);

    MPI_Finalize();
    return 0;
}