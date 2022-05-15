# Testausdokumentti

Sovellus on testattu sekä automaattisilla yksikkö- ja integraatiotesteillä JUnit-kirjaston avulla, sekä manuaalisesti järjestelmätasolla.

### Sovelluslogiikka

Käyttöliittymää lukuunottamatta sovelluslogiikka koostuu pääosin pakkauksista [game](https://github.com/kbjakex/ot-harjoitystyo/tree/main/rollingball/src/main/java/rollingball/game) 
ja [functions](https://github.com/kbjakex/ot-harjoitystyo/tree/main/rollingball/src/main/java/rollingball/functions). 
Pakkauksen `functions` luokkia testataan kattavasti [täällä](https://github.com/kbjakex/ot-harjoitystyo/blob/main/rollingball/src/test/java/rollingball/FunctionParserTest.java),
ja koko pelisimulaatiota - ja siten koko `game`-pakkausta - [täällä](https://github.com/kbjakex/ot-harjoitystyo/blob/main/rollingball/src/test/java/rollingball/GameSimulatorTest.java).

Lisäksi sovelluksessa on luokka, joka tallentaa pelaajan edistymisen tiedostoon. Tätä testataan [täällä](https://github.com/kbjakex/ot-harjoitystyo/blob/main/rollingball/src/test/java/rollingball/UserProgressDaoTest.java).

### Testauskattavuus

Sovelluksen rivikattavuus on 95% ja haaraumakattavuus 89%. Käyttöliittymä on jätetty ulos raportista.
<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/codecov.png">

### Järjestelmätestaus

Sovelluksen järjestelmätestaus on suoritettu manuaalisesti.

### Asennus ja konfigurointi

Sovellusta on kokeiltu asentaa käyttöohjeen mukaisesti sekä Windows 10 -laitteella että yliopistolta lainatulla linux-läppärillä.

Sovellus on testattu sekä silloin, kun tallennustiedostoa ei vielä löydy, ja silloin, kun peliä on jo aiemmin pelattu. 

### Toiminnallisuudet

Kaikki määrittelydokumentissa luetellut toiminnallisuudet on manuaalisesti testattu, myös virheellisillä ja puuttuvilla syötteillä.

## Sovellukseen jääneet laatuongelmat

Alkuperäisen suunnitelman mukaisesti peli sisältää toiminnallisuutta, joka aiheuttaa ongelmia etenkin törmäystunnistusten kanssa.
Ongelmaa ei ole realistista korjata, vaan toiminnallisuus tulisi enemmin poistaa ja törmäyksiin liittyvä koodi sitten kirjoittaa uudelleen.

Vaikka määrittelydokumentti ei tätä vaadikaan, sovellus ei tallenna käyttäjän syötteitä ennen kuin käyttäjä pääsee kentästä läpi.
Tämä tarkoittaa, ettei kesken pelin voi kentästä poistua ja myöhemmin palata takaisin, mikä heikentää käyttökokemusta.
