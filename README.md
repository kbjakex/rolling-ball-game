# Ohjelmistotekniikka, harjoitustyö

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä.

Meneillään on viikko 3, eli sovellus on vielä hyvin vaiheessa.

## Dokumentaatio

[Vaatimusmäärittely](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/vaatimusmaarittely.md)

[Työaikakirjanpito](https://github.com/kbjakex/ot-harjoitystyo/blob/main/dokumentaatio/tyoaikakirjanpito.md)

### Testaus

Testit suoritetaan seuravasti:

```
mvn test
```

Testikattavuusraportti luodaan seuraavasti:

```
mvn jacoco:report
```

Kattavuusraportti luodaan tiedostoon `index.html` polussa `target/site/jacoco/`.
