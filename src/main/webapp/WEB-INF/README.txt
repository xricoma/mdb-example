
Se genera un WAR con varios MDB explicados a continuación.
Generación: mvn package -f "/Users/xricoma/Documents/00-BATIBURRILLO/99-APLICACIONES/16-TEST2/mdb-example/pom.xml"

El dominio debe tener creados una serie de recursos JMS para que funcionen (indicados a continuación).

*****************************************
ExampleMDB.java -> OK
MessageSender.java
JAXRSConfiguration.java

test-dq1
test-cfg

No transaccional
*****************************************
MyMDB.java -> OK

test-dq2
test-cfgXA

Transaccional

Recibe el mensaje y lo envia a "test-dq2"
*****************************************
ExampleMDB_JMSReplyTo.java -> OK

test-dq3
test-cfg

No transaccional

Recibe mensajes y los envia a la cola indicada en "ReplyTo" en el mensaje entrante
*****************************************
TextMDB.java -> OK
SendRecvClient.java
weblogic-ejb-jar.xml
ejb-jar.xml

Configuración de la cola/conection factory desde el fichero "weblogic-ejb-jar.xml" y "ejb-jar.xml"
test-dq4
test-cfg

No transaccional

Recibe mensajes y los envia a la cola indicada en "ReplyTo" en el mensaje entrante.
*****************************************
