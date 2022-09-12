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
    MPI_Scatter(&A[0][0], l * m, MPI_INT, &local_A[0][0], l * m, MPI_INT, root, MPI_COMM_WORLD);
    // Slanje matrice B svim procesima
    MPI_Bcast(&B[0][0], m * n, MPI_INT, root, MPI_COMM_WORLD);

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
    MPI_Reduce(&in, &out, 1, MPI_2INT, MPI_MAXLOCK, root, MPI_COMMM_WORLD);
    // To se prosledi svim procesima
    MPI_Bcast(&out, 1, MPI_2INT, root, MPI_COMM_WORLD);

    // Skupi se matrica C[k][n]
    MPI_Gather(&local_C[0][0], l * n, MPI_INT, &C[0][0], l * n, MPI_INT, out.rank, MPI_COMM_WORLD);
    // Nadje se proizvod elemenata svake kolone
    MPI_Reduce(&local_mul[0], &final_mul[0], m, MPI_INT, MPI_PROD, out.rank, MPI_COMM_WORLD);

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