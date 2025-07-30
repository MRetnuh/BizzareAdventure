## [3.3.6] - 2025-07-30
### Añadido
- Creacion de la clase "Configuracion" dentro del paquete juego.
- Modificacion de como su manipula la musica dentro de partida, menu y principal.
- Agregado de funciones para la clase Musica.
## [3.2.6] - 2025-07-29
### Añadido
- Creacion de clase EstiloTexto para ajustar los textos de botones y labels (estilo, tamaño, color, etc) sin necesidad de repetir codigo y reutilizandolo. Reestructuramiento de codigo

## [3.2.5] - 2025-07-28
### Modificado
- Modificacion del cartel de GameOver, siendo ahora mas atractivo
- Modificacion en los labels (textos). Ahora son mas atractivos, tienen mejor nitidez/calidad y son pixel art. 

## [3.2.4] - 2025-07-27
### Añadido
- Implementacion de gravedad
- Implementacion de limites en el mapa. El jugador y la camara chocan con los limites, de modo que no pueden seguir avanzando
- Implementacion de la posibilidad de que el jugador muera. En caso de caer al vacio, el personaje muere, aparece un cartel de derrota y tras unos segundos, el codigo termina

## [2.2.4] - 2025-07-25
### Añadido
- Modificacion del mapa base y de las colisiones del mapa
- Reestructuramiento de la musica. Creacion de clase Musica

## [2.2.3] - 2025-07-24
### Añadido
- Implementacion de colisiones basicas en el archivo .tmx
- Implementacion de deteccion de colisiones en Personaje y Partida 

## [2.2.2] - 2025-07-24
### Añadido
- Implementación de eleccion de personaje al azar al inicio de la partida mediante el uso de un sprite de prueba junto al sprite de Akame.
- Implementación de clase Jugador y uso de herencia para administrar las funciones basicas compartidas y las propias de cada hija como sus sprites

## [2.1.2] - 2025-07-23
### Añadido
- Implementación del personaje en el mapa y ahora puede moverse por el mapa. Además, la camara lo sigue.
- Implementación de la musica con sus respectivas teclas para poder modificar el volumen y silenciarlo, ademas se colocan 2 musicas una para el menu y otra para la partida.
- Portada del juego añadida
- Launcher del juego a pantalla completa
### Modificado
- Eliminación de archivos innecesarios como el logotipo de libGDX, iniciarPartida, etc
- Modificacion y ordenamiento de archivos.

## [1.1.2] - 2025-06-30
### Modificado
- Se movieron los archivos de modo que ya no esta la carpeta Proyecto

## [1.1.1] - 2025-05-25
### Modificado
- Se actualizó `IniciarPartida.java`.
- Se realizaron cambios en los assets: agregación de nuevos archivos y eliminación de archivos innecesarios.


## [1.1.0] - 2025-05-07
### Añadido
- Creacion de una pantalla al ejecutar el archivo con dos botones. Uno para salir del juego y otro que te envia al mapa.
- Mapa de prueba para ver como importar un mapa creado en tiled a eclipse


## [1.0.0] - 2025-05-06
### Añadido
- Creacion del proyecto 
