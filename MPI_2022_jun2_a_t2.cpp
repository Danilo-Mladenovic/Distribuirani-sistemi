#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

#define n 3
#define k 4
#define q 2

int main(int argc, char** argv)
{
    int size, rank, root = 0;
    MPI_Request req;
    MPI_Status stat;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int A[n][k], B[k][n], C[n][n];
    int local_column_prod[n], final_column_prod[n];
    int local_A[n][q], local_B[q][n], local_C[n][n];

    struct {
        int value;
        int rank;
    } in = { INT_MIN, rank }, out;


    if (rank == root)
    {
        for (int i = 0; i < n; i++)
            for (int j = 0; j < k; j++)
                A[i][j] = i + j;

        for (int i = 0; i < k; i++)
            for (int j = 0; j < n; j++)
                B[i][j] = 1;
    }

    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
            local_C[i][j] = 0;
        local_column_prod[i] = 1;
    }


    // Slanje q kolona matrice A
    if (rank == root)
        for (int i = 0; i < size; i++)
            for (int j = 0; j < n; j++)
                MPI_Isend(&A[j][i * q], q, MPI_INT, i, 0, MPI_COMM_WORLD, &ret);
    
    for (int i = 0; i < n; i++)
        MPI_Recv(&local_A[i][0], q, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);

    // Slanje q vrsta matrice B
    MPI_Scatter(&B[0][0], q * n, MPI_INT, &local_B[0][0], q * n, MPI_INT, root, MPI_COMM_WORLD);


    for (int i = 0; i < n; i++)
    {
        for (int j = 0; j < n; j++)
        {
            for (int x = 0; x < q; x++)
            {
                local_C[i][j] += local_A[i][x] * local_B[x][j];
            }
        }
    }


    MPI_Finalize();
    return 0;
}