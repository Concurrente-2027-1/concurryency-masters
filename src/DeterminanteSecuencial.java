public class DeterminanteSecuencial {
    static int determinante;
    static int n_prueba = 3;
    static int matriz_prueba[][] = { { 1, 2, 2 }, { 1, 0, -2 }, { 3, -1, 1 }};
    
    public static int determinanteMatriz3x3(int matriz[][], int n_prueba) {
        // Calculamos las diagonales principales 
        int part1 = matriz[0][0] * matriz[1][1] * matriz[2][2];
        int part2 = matriz[1][0] * matriz[2][1] * matriz[0][2];
        int part3 = matriz[2][0] * matriz[0][1] * matriz[1][2];
        
        // Calculamos las diagonales inversas 
        int part4 = matriz[2][0] * matriz[1][1] * matriz[0][2];
        int part5 = matriz[1][0] * matriz[0][1] * matriz[2][2];
        int part6 = matriz[0][0] * matriz[2][1] * matriz[1][2];
        
        // Sumamos y restamos
        return part1 + part2 + part3 - part4 - part5 - part6;
    }

	public static void main(String[] args) {
		long startTime = System.nanoTime();
		determinante = determinanteMatriz3x3(matriz_prueba, n_prueba);
		long endTime = System.nanoTime();
        
        System.out.println("Program took " +
                (endTime - startTime) + "ns, result: " + determinante);
	}
}
