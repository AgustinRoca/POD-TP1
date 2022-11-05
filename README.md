# Trabajo Práctico 1: Elecciones

Ubicarse en el directorio del proyecto y ejecutar:
```bash
$> mvn clean install
```

### Servidor
  Para ejecutar el servidor descomprimir el tar.gz y ejecutar los scripts
  run-registry.sh y run-server.sh en ese orden
  ```bash
  $> run-registry.sh
  $> run-server.sh
  ```
  El servidor se ejecuta en el puerto 1099

### Clientes
  Para ejecutar cualquier de los clientes descomprimir el tar.gz y ejecutar
  alguno de los siguientes scripts. En todos los casos se debe pasar como argumento -DserverAddress=xx.xx.xx.xx:yyyy donde xx.xx.xx.xx es la ip del del servidor e yyyy es el puerto (en este server el 1099)

#### Cliente de Administracion
  ```bash
  $> ./run-management -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
  ```
  Donde los valores posibles de actionName son:
  - open : Abre los comicios
  - state : Consulta el estado de los comicios
  - close : Cierra los comicios
  
#### Cliente de Fiscalizacion
  ```bash
  $> ./run-fiscal -DserverAddress=xx.xx.xx.xx:yyyy -Did=pollingNumber -Dparty=partyName
  ```
  Donde pollingNumber es la mesa y partyName el nombre del partido

#### Cliente de Consulta
  ```bash
  $> ./run-query -DserverAddress=xx.xx.xx.xx:yyyy [ -Dstate=stateName | -Did=pollingPlaceNumber ] -DoutPath=fileName
  ```
  Donde stateName es el nombre de la provincia, pollingPlaceNumber y fileName el path del archivo de salida. El query falla si se pasan ambos -Dstate y -Did
  
#### Cliente de Votacion
  ```bash
  $> ./run-vote -DserverAddress=xx.xx.xx.xx:yyyy -DvotesPath=fileName
  ```
  Donde fileName es el path del archivo csv de las votos
