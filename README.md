# Ohjelmistotekniikka, harjoitustyö

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä.

Meneillään on viikko 5. Peli on pelattavissa, mutta kentät ovat alkukantaisia (liian helppoja / tylsiä). Pisteytys ja edistymisen tallennus puuttuu ja kenttävalikko on alkukantainen.

## Dokumentaatio

[Vaatimusmäärittely](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/vaatimusmaarittely.md)

[Työaikakirjanpito](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/tyoaikakirjanpito.md)

[Arkkitehtuurikuvaus](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/arkitehtuuri.md)

## Releaset

[Viikko 5](https://github.com/kbjakex/ot-harjoitystyo/releases/tag/viikko5)

### Testaus

Testit suoritetaan seuravasti:

```console
mvn test
```

Testikattavuusraportti luodaan seuraavasti:

```console
mvn test jacoco:report
```
Raportti luodaan tiedostoon `index.html` polussa `target/site/jacoco/`.

Checkstyle-raportti luodaan seuraavasti:
```console
mvn jxr:jxr checkstyle:checkstyle
```
Raportti luodaan tiedostoon `checkstyle.html` polussa `target/site/`.

### Suoritus

Suoritettavan jar-tiedoston voi generoida `target/`-kansioon komennolla
```console
mvn package
```
ja ohjelman voi käynnistää suoraan komentoriviltä komennolla
```console
mvn compile exec:java -Dexec.mainClass=rollingball.ui.RollingBall
```

## Javan versio

Koodi on kirjoitettu ja testattu ainoastaan Java 18:lla. Voit ladata Java 18:n [täältä](https://jdk.java.net/18/).
