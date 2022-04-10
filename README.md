# Ohjelmistotekniikka, harjoitustyö

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä.

Meneillään on viikko 3, eli sovellus on vielä hyvin vaiheessa.

## Dokumentaatio

[Vaatimusmäärittely](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/vaatimusmaarittely.md)

[Työaikakirjanpito](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/tyoaikakirjanpito.md)

### Testaus

Testit suoritetaan seuravasti:

```console
mvn test
```

Testikattavuusraportti luodaan seuraavasti:

```console
mvn jacoco:report
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

Java 10 on todennäköisesti riittävän uusi, mutta koodi on kirjoitettu ja testattu ainoastaan Java 17:lla. Voit ladata Java 17:n [täältä](https://jdk.java.net/17/).
