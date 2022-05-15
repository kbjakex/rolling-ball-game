# Vaatimusmäärittely

## Sovelluksen tarkoitus

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä. 

## Käyttöliittymäluonnos

Pelissä on kolme näkymää.

Peli käynnistyy "päävalikkoon", josta päästään joko jatkamaan aiempaa pelikertaa, valitsemaan kenttää, tai poistumaan pelistä.
"Kenttävalikkonäkymässä" on pelin kaikki kentät perättäin vaakatasossa, joita painamalla päästään pelaamaan haluttua kenttää.
"Pelinäkymästä" voi siirtyä joko takaisin päävalikkoon, tai kentän läpäistyä seuraavaan kenttään.

<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/kayttoliittymadraft.jpg" width="800">

## Toiminnallisuus

* Päävalikko
  * Päävalikosta voi jatkaa pelaamista siitä, mihin jäi, tai ensimmäisestä kentästä, jos ei ole aiemmin pelannut.
  * Päävalikosta voi myös siirtyä kenttävalikkoon tai poistua pelistä
* Kenttävalikko
  * Kenttävaliko näyttää, montako kenttää pelaaja on ratkaissut, montako on jäljellä, ja paljonko on saanut tähtiä ratkaisemistaan kentistä (0-3).
* Pelinäkymä
  * Käyttäjä voi syöttää yhtälöitä muotoa `y = f(x)` (`y=` on implisiittinen)
  * Käyttäjä voi rajoittaa, millä ehdolla yhtälö piirretään, käyttämällä operaattoreita `<`, `<=`, `>=` ja `>` (esim. `1 < x < 5` tai `x >= 0`).
  * Yhtälöt voivat sisältää muuttujia `x` ja `t` sekä vakioita `e` ja `pi`. `t` alkaa jokaisella yrityksellä nollasta ja mittaa sekunteja.
  * Yhtälöissä voi käyttää ainakin funktioita sin, cos, tan, asin, acos, atan, sinh, cosh, tanh, ln, lg, sqrt, abs, floor, ceil, round, sign, min, max, atan2. 
  * Käyttäjä muokata aiemmin lisäämiään yhtälöitä.
  * Käyttäjä voi poistaa aiemmin lisäämiään yhtälöitä.
  * Käyttäjän syöttämät yhtälöt piirtyvät koordinaattiruudukolle; jokainen eri värillä, jotta käyttäjälle on tarvittaessa selvää, mitä yhtälöä muokata.
  * Käyttäjä voi aloittaa pelin play-painikkeesta, jolloin pelin pallohahmo alkaa pyörimään oikealle. Hahmo pyörii kuvaajien päällä.
  * Käyttäjä voi epäonnistua suorituksessaan törmäämällä esteeseen tai antamalla pallohahmon poistua pelialueelta.
  * Käyttäjä saa kentän ratkaistuaan seuraavan kentän auki, jos kenttiä on vielä jäljellä.
  * Kun käyttäjä ratkaisee kentän, tallennetaan käyttäjän syötteet sekä pisteet kyseisestä kentästä tiedostoon `rollingballdata.dat`.
* Muuta
  * Peli sisältää 5 kenttää
  * "Exit"/"Back" -napit siirtyvät aina edelliseen näkymään.
  * Peli lataa käynnistyessä pelaajan edistymisen tiedostosta `rollingballdata.dat`. Jos tiedosto on korruptoitunut, kysyy sovellus käyttäjältä, halutaanko tiedosto korvata uudella, tyhjällä tiedostolla.

## Jatkokehitysideoita

Peliä voisi tulevaisuudessa jatkokehittää seuraavasti:

- kun pelaaja onnistuneesti ratkaisee kentän, voisi ratkaisuun käytetyt yhtälöt ja saadut pisteet lähettää esim. Google Sheetsiin.
- peli voisi Google Sheetsistä kentän ratkaistua pelaajan halutessa hakea muiden ratkaisut kenttään, jotta tuloksia ja ratkaisuja voisi vertailla
- nämä tulokset voisi järjestää lähetysajan tai ratkaisun pistemäärän perusteella
- kenttiin voisi lisätä elementtejä kuten trampoliineja, nopeustehosteita (eng. speed boost) ja suunnanmuuttajia (kääntää pallon pyörimissuunnan)
- peli voisi hyväksyä myös parametrisiä yhtälöitä
- peliin voisi lisätä myös kenttäeditorin, jonka avulla voisi luoda ja jakaa omia kenttiä muille pelattavaksi
- peliin voisi lisätä musiikkia ja ääniefektejä
- lisää kenttiä!
