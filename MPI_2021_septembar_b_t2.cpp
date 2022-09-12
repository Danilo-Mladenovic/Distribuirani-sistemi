#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

#define k 6
#define m 3
#define n 5
#define l 2

int main(int argc, char** argv)
{
    int size, rank, root = 0;
    MPI_Request req;
    MPI_Status stat;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int A[k][m], B[m][n], C[k][n];
    int local_A[l][m], local_C[l][n];
    int local_mul[m], final_mul[m];

    for (int i = 0; i < m; i++)
        local_mul[i] = 1;

    struct {
        int value;
        int rank;
    } in = { INT_MIN, rank }, out;

    if (rank == root)
    {
        for (int i = 0; i < k; i++)
            for (int j = 0; j < m; j++)
                A[i][j] = i + j;
        
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                B[i][j] = 2 * i + j;
    }

    // Slanje po l vrsta matrice A svakom procesu
    // MPI_Scatter(&A[0][0], l * m, MPI_INT, &local_A[0][0], l * m, MPI_INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&A[p * l][0], l * m, MPI_INT, i, 0, MPI_COMM_WORLD, &req);
    MPI_Recv(&local_A[0], l * m, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);


    // Slanje matrice B svim procesima
    // MPI_Bcast(&B[0][0], m * n, MPI_INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&B[0][0], m * n, MPI_INT, i, 0, MPI_COMM_WORLD, &req);
    MPI_Recv(&B[0][0], m * n, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);


    for (int i = 0; i < l; i++)
    {
        for (int j = 0; j < n; j++)
        {
            local_C[i][j] = 0;
            for (int x = 0; x < m; x++)
                local_C[i][j] += local_A[i][x] * B[x][j];
            
            if (local_C[i][j] > in.value)
                in.value = local_C[i][j];
        }

        for (int j = 0; j < m; j++)
            local_mul[j] *= local_A[i][j];
    }

    // Nalazenje procesa sa maksimum vrednosti - tu se prikazuje rezultat
    // MPI_Reduce(&in, &out, 1, MPI_2INT, MPI_MAXLOCK, root, MPI_COMMM_WORLD);
    MPI_Isend(&in, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &req);
    if (rank == root)
    {
        MPI_Recv(&out, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &stat);

        for (int i = 1; i < size; i++)
        {
            MPI_Recv(&in, 1, MPI_2INT, i, 0, MPI_COMM_WORLD, &stat);
            if (in.value > out.value)
                out = in;
        }
    }

    // To se prosledi svim procesima
    // MPI_Bcast(&out, 1, MPI_2INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&out, 1, MPI_2INT, i, 0, MPI_COMM_WORLD, &req);
    MPI_Recv(&out, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &stat);


    // Skupi se matrica C[k][n]
    // MPI_Gather(&local_C[0][0], l * n, MPI_INT, &C[0][0], l * n, MPI_INT, out.rank, MPI_COMM_WORLD);
    MPI_Isend(&local_C[0][0], l * n, MPI_INT, out.rank, 0, MPI_COMM_WORLD, &rank);
    if (rank == out.rank)
        for (int i = 0; i < size; i++)
            MPI_Recv(&C[i * l][0], l * n, MPI_INT, i, 0, MPI_COMM_WORLD, &stat);

    // Nadje se proizvod elemenata svake kolone
    // MPI_Reduce(&local_mul[0], &final_mul[0], m, MPI_INT, MPI_PROD, out.rank, MPI_COMM_WORLD);
    MPI_Isend(&local_mul[0], m, MPI_INT, out.rank, 0, MPI_COMM_WORLD, &rank);
    if (rank == out.rank)
    {
        MPI_Recv(&final_mul[0], m, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);
        for (int i = 1; i < size; i++)
        {
            MPI_Recv(&local_mul[0], m, MPI_INT, i, 0, MPI_COMM_WORLD, &stat);
            for (int j = 0; j < m; j++)
                final_mul[j] *= local_mul[j];
        }
    }

    if (rank == out.rank)
    {
		for (int i = 0; i < k; i++)
			for (int j = 0; j < n; j++)
				printf(C[i][j]);

		for (int i = 0; i < m; i++)
			printf(final_mul[i]);
    }

    MPI_Finalize();
    return 0;
}