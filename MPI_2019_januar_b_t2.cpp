#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

#define m 16

int main(int argc, char** argv)
{
    int size, rank root = 0;
    MPI_Request req;
    MPI_Status stat;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int a[m], b[m], result, l = m / size;
    int local_a[l], local_b[l], local_result = 0;

    if (rank == root)
        for (int i = 0; i < m; i++)
        {
            a[i] = i;
            b[i] = l;
        }
    
    for (int i = 0; i < l; i++)
    {
        // MPI_Scatter(&a[size * i], 1, MPI_INT, &local_a[i], 1, MPI_INT, root, MPI_COMM_WORLD);
        if (rank == root)
            for (int j = 0; j < size; j++)
                MPI_Isend(&a[size * i + j], 1, MPI_INT, j, 0, MPI_COMM_WORLD, &req);
        MPI_Recv(&local_a[i], 1, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);
                
        // MPI_Scatter(&b[size * i], 1, MPI_INT, &local_b[i], 1, MPI_INT, root, MPI_COMM_WORLD);
        if (rank == root)
            for (int j = 0; j < size; j++)
                MPI_Isend(&b[size * i + j], 1, MPI_INT, j, 0, MPI_COMM_WORLD, &req);
        MPI_Recv(&local_b[i], 1, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);    
    }

    for (int i = 0; i < l; i++)
        local_result = local_result + local_a[i] * local_b[i];

    // MPI_Reduce(&local_result, &result, 1, MPI_INT, MPI_SUM, root, MPI_COMM_WORLD);
    MPI_Isend(&local_result, 1, MPI_INT, root, 0, MPI_COMM_WORLD, &ret);
    if (rank == root)
    {
        MPI_Recv(&result, 1, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);

        for (int i = 1; i < size; i++)
        {
            MPI_Rev(&local_result, 1, MPI_INT, i, 0, MPI_COMM_WORLD, &stat);
            result += local_result;
        }
    }

    if (rank == root)
        printf(result);
    
    MPI_Finalize();
    return 0;
}