# Changelog

## Viikko 3

- Päävalikossa on nyt kaikki tarvittava, ja napit toimivat
- Lisättiin matemaattisten lausekkeiden lukemista varten kaksi luokkaa
- Testattiin suurelta osin näiden luokkien toimintaa

## Viikko 4

- Yhtälöiden piirtäminen pelikentälle toimii ja yhtälöitä voi poistaa
- Yhtälöt voivat nyt sisältää muuttujia, vakioita ja funktiokutsuja
- Eri yhtälöt piirretään eri väreillä
- Yhtälöiden lukemista testataan entistä kattavammin
- Pelitilanteen levylle tallentamista varten on DAO-hahmotelma

## Viikko 5

- Lisättiin pallo, törmäystunnistukset & algoritmi pallon korkeuden laskemiselle
- Lisättiin maalilippu ja "voitto"-toiminnallisuus
- Lisättiin neljä kenttää
- Lisättiin kenttävalikko
- Huoliteltiin käyttöliittymän ulkoasua päävalikossa ja pelinäkymässä
- Lisättiin start/stop toiminnallisuus
- Lisättiin yhtälöiden rajaukset (esim. `sin(t) < x < 8`)

## Viikko 6

- Pelitilanne tallentuu nyt tiedostoon `rollingballdata.dat`, ja peliä voi sulkemisen jälken jatkaa siitä, mihin jäi
- Kenttävalikko on hiottu jotakuinkin vastaamaan suunnitelmaa graafisesti
- Kentän läpäisystä saa pisteitä, joiden perusteella määräytyy tähtien määrä
- Kaikki kentät eivät enää ole automaattisesti auki, vaan lukitun kentän saa auki ratkaisemalla edellisen kentän
- Kaikki 5 kenttää ovat nyt pelissä ja kenttien vaikeustaso on nyt riittävä.
- Javadoc-dokumentaatio on aloitettu; `game`- ja `ui`-kansiot ovat täysin dokumentoituja.
- Peli ei enää käytä oikeaa aikaa ajan mittaukseen, minkä ansiosta peli on nyt reilu myös hitailla koneilla, deterministinen ja yksikkötestattava. Jokainen kenttä onkin nyt yksikkötestattu malliratkaisun avulla.
- Lisättiin käyttöohje
