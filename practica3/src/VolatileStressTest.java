public class VolatileStressTest {
    // Variables compartidas SIN volatile para inducir fallos de visibilidad y reordenamiento
    static int a = 0;
    static int b = 0;
    static boolean flag = false;

    public static void main(String[] args) throws InterruptedException {
        long iteraciones = 0;
        long inconsistencias = 0;

        System.out.println("Iniciando prueba de estrés... Presiona Ctrl+C para detener.\n");

        while (true) {
            iteraciones++;

            // 1. Reset de estado antes de cada prueba
            a = 0;
            b = 0;
            flag = false;

            // Arreglos de un solo elemento para capturar lecturas del hilo sin usar sincronización
            final int[] leidoA = new int[1];
            final int[] leidoB = new int[1];
            final boolean[] leidoFlag = new boolean[1];

            // 2. Hilo Escritor
            Thread escritor = new Thread(() -> {
                a = 100;
                b = 200;
                flag = true; // Sin volatile, esta línea puede reordenarse antes de 'a' y 'b'
            });

            // 3. Hilo Lector
            Thread lector = new Thread(() -> {
                if (flag) {
                    leidoA[0] = a;
                    leidoB[0] = b;
                    leidoFlag[0] = true;
                }
            });

            escritor.start();
            lector.start();

            escritor.join();
            lector.join();

            // 4. Verificación del Modelo de Memoria (JMM)
            // Si vio 'flag = true' pero 'a' o 'b' valen 0, se prueba el fallo de visibilidad o reordenamiento
            if (leidoFlag[0] && (leidoA[0] != 100 || leidoB[0] != 200)) {
                inconsistencias++;
                System.out.printf("¡INCONSISTENCIA DETECTADA! Iteración %,d -> flag: true | a: %d | b: %d%n",
                        iteraciones, leidoA[0], leidoB[0]);
            }

            // Reporte periódico de progreso
            if (iteraciones % 100_000 == 0) {
                System.out.printf("Iteraciones ejecutadas: %,d | Fallos de JMM detectados: %d%n", 
                        iteraciones, inconsistencias);
            }
        }
    }
}