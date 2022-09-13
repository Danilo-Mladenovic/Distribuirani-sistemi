#include <stdio.h>
#include <stdlib.h>
#include <mpi.h>

#define k 3
#define m 4
#define l 2

int main(int argc, char** argv)
{
    int size, rank, root = 0;
    MPI_Request req;
    MPI_Status stat;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int A[k][m], b[m], c[k];
    int local_A[k][l], local_b[b], local_c[k];
    int local_row_sum[k], row_sum[k];

    // Koristi se za nalazenje procesa koji sadrzi maksimalnu vrednost ele u A
    struct {
        int value;
        int rank;
    } in = { INT_MIN, rank }, out;

    // Inicijalizacija matrice A i vektora b
    if (rank == root)
        for (int j = 0; j < m; j++)
        {
            for (int i = 0; i < k; i++)
                A[i][j] = i + j * k;
            
            b[j] = j;
        }
    
    // PTP slanje kolona matrice A
    if (rank == root)
        for (int i = 0; i < size; i++) // Za broj procesa
            for (int j = 0; j < k; j++) 
                MPI_Isend(&A[j][i * l], l, MPI_INT, i, 0, MPI_COMM_WORLD, &req);

    // Primanje PTP kolona matrice A
    for (int i = 0; i < k; i++)
        MPI_Recv(&local_A[i][0], l, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);

    // Slanje delova vektora b
    // MPI_Scatter(&b[0], l, MPI_INT, &local_b[0], l, MPI_INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&b[i * l], l, MPI_INT, i, 0, MPI_COMM_WORLD, &req);
    MPI_Recv(&local_b[0], l, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);

    
    for (int i = 0; i < k; i++)
    {
        local_c[i] = 0;
        local_row_sum[i] = 0;

        for (int j = 0; j < l; j++)
        {
            // Racuna lokalni rezultujuci vektor
            local_c[i] += local_A[i][j] * local_b[j];
            // Racuna lokalnu sumu elemenata vrste
            local_row_sum[i] += local_A[i][j];

            // Proverava da li je trenutna vrednost maksimalna
            if (local_A[i][j] > in.value)
                in.value = local_A[i][j];
        }
    }

    // Pronalazi proves sa najvecim elementom u matrici i stavlja tu informaciju u root
    // MPI_Reduce(&in, &out, 1, MPI_2INT, MPI_MAXLOCK, root, MPI_COMM_WORLD);
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
    

    // Root javlja svima koji proces ima najveci element u matrici
    // MPI_Bcast(&out, 1, MPI_2INT, root, MPI_COMM_WORLD);
    if (rank == root)
        for (int i = 0; i < size; i++)
            MPI_Isend(&out, 1, MPI_2INT, i, 0, MPI_COMM_WORLD, &req);
    MPI_Recv(&out, 1, MPI_2INT, root, 0, MPI_COMM_WORLD, &stat);


    // Sabiranje lokalnih vektora c
    // MPI_Reduce(&local_c[0], &c[0], k, MPI_INT, MPI_SUM, out.rank, MPI_COMM_WORLD);
    MPI_Isend(&local_c[0], k, MPI_INT, out.rank, 0, MPI_COMM_WORLD, &req);
    if (rank == out.rank)
    {
        MPI_Recv(&c[0], k, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);
        for (int i = 0; i < size, i++)
        {
            MPI_Recv(&local_c[0], k, MPI_INT, i, 0, MPI_COMM_WORLD, &stat);
            for (int j = 0; j < k; j++)
                c[j] += local_c[j];
        }
    }


    // Sabiranje elemenata svake vrste matrice
    // MPI_Reduce(&local_row_sum[0], row_sum[0], k, MPI_INT, MPI_SUM, out.rank, MPI_COMM_WORLD);
    MPI_Isend(&local_row_sum[0], k, MPI_INT, out.rank, 0, MPI_COMM_WORLD, &req);
    if (rank == out.rank)
    {
        MPI_Recv(&row_sum[0], k, MPI_INT, root, 0, MPI_COMM_WORLD, &stat);
        for (int i = 0; i < size; i++)
        {
            MPI_Recv(&local_row_sum[0], k, MPI_INT, i, 0, MPI_COMM_WORLD, &stat);
            for (int j = 0; j < k; j++)
                row_sum[j] += local_row_sum[j];
        }
    }


    if (rank == root)
    {
        // uz formatiranje naravno...
		for (int i = 0; i < k; i++)
			printf(c[i]);

		for (int i = 0; i < k; i++)
			printf(row_sum[i]);
    }

    MPI_Finalize();
    return 0;
}