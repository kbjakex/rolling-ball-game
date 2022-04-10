# Arkkitehtuurikuvaus

Ohjelman rakenne on hierarkkinen: käyttöliittymäluokat (erityisesti GameRenderer.java ja GraphStorage.java) käyttävät `expressions` ja `dao` -paketteja, jotka eivät tiedä toisistaan.

<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/pakkauskaavio.png" width="500">

Käyttöliittymää lukuunottamatta ohjelman keskeisiä luokkia ovat `expressions`-paketin rajapinta `Expr`, jonka avulla kaikki matemaattiset yhtälöt esitetään koodissa. 

GraphStorage on pelinaikainen säilöntäpaikka käyttäjän syöttämille yhtälöille. 

`ui`-paketin `GameRenderer.java` luo ja käyttelee GraphStoragea ja säilöttyjä `Expr`-olioita. `expressions`-paketin `ExpressionParser` luo kaikki ohjelman `Expr`-oliot.

<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/luokkakaavio.png" width="500">

