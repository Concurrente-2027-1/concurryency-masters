# Práctica 4

## Reporte PDF
Se ubica en la ruta [`latex/practica4.pdf`](latex/practica4.pdf) o dando [click acá](latex/practica4.pdf). 
## Programas
Se encuentran en la carpeta [src](src). Dentro de esa carpeta se encuentran los archivos que nos compartió la profesora para poder hacer las pruebas. Y además, se crearon nuevos archivos para hacer las pruebas más fácilmente. Hicimos un archivo para cada candado o técnica diferente, tanto para el contador como para la matriz.

Por ejemplo, para ejecutar el spinlock ALock hicimos:
```
javac RunSpinALock.java
java RunSpinALock <numero tareas> <numero hilos> 
```

Para ejecutar el spinlock ALock para hacer operaciones con matrices, ejecutamos:
```
javac RunSpinMatrixALock.java
java RunSpinMatrixALock <numero tareas> <numero hilos> 
```

Análogamente, se ejecutan los otros archivos de los otros spinlocks (escribiendo el nombre de los archivos como se muestran en la carpeta [src](src)).
