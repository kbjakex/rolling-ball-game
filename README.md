# Ohjelmistotekniikka, harjoitustyö

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä.

Meneillään on viikko 7. Peli on valmis.

Molemmat GitHub-käyttäjät ovat minun. Projektia on tehty kahdella eri koneella ja meneillään on siirtymävaihe uudelle käyttäjälle.

## Dokumentaatio

[Vaatimusmäärittely](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/vaatimusmaarittely.md)

[Työaikakirjanpito](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/tyoaikakirjanpito.md)

[Arkkitehtuurikuvaus](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/arkitehtuuri.md)

[Käyttöohje](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/kayttoohje.md)

[Muutosloki](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/changelog.md)

[Testausdokumentaatio](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/testausdokumentti.md)

## Releaset

[Viikko 7](https://github.com/kbjakex/ot-harjoitystyo/releases/tag/Loppupalautus)

[Viikko 6](https://github.com/kbjakex/ot-harjoitystyo/releases/tag/viikko6)

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

## Javadoc
Javadocin voi generoida komennolla
```console
mvn javadoc:javadoc
```
jonka jälkeen dokumentaatio löytyy polusta `target/site/apidocs/index.html`.

## Javan versio

Koodi on kirjoitettu ja testattu ainoastaan Java 18:lla. Voit ladata Java 18:n [täältä](https://jdk.java.net/18/).
