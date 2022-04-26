# Vaatimusmäärittely

## Sovelluksen tarkoitus

Sovellus on peli, jossa ohjataan kaksiulotteisessa avaruudessa oikealle kulkeva pallo lähdöstä maaliin kirjoittamalla yhtälöitä, joilla määritellään pallolle pintoja, joiden päällä pyöriä. 

## Käyttöliittymäluonnos

Pelissä on kolme näkymää.

Peli käynnistyy "päävalikkoon", josta päästään joko jatkamaan aiempaa pelikertaa, valitsemaan kenttää, tai poistumaan pelistä.
"Kenttävalikkonäkymässä" on pelin kaikki kentät perättäin vaakatasossa, joita painamalla päästään pelaamaan haluttua kenttää.
"Pelinäkymästä" voi siirtyä joko takaisin päävalikkoon, tai kentän läpäistyä seuraavaan kenttään.

<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/kayttoliittymadraft.jpg" width="800">

## Perusversion tarjoama toiminnallisuus

- käyttäjä voi navigoida näkymästä toiseen eli selata kenttiä ja aloittaa pelaamisen (**tehty**)
- käyttäjä näkee kenttävalikkonäkymästä oman edistymisensä
- käyttäjä voi jatkaa päävalikon "start"-painikketta käyttäen siitä kentästä, mihin viime pelikerralla jäi, tai ensimmäisestä, jos ei ole ennen pelannut
- käyttäjä voi syöttää pelinäkymässä yhtälöitä muotoa `y = f(x)` tai `x = f(y)` (**tehty**)
- yhtälöt piirtyvät pelikentälle graafisen laskimen tapaan (**tehty**)
- pelin pallohahmo voi pyöriä näiden yhtälöiden muodostamien käyrien päällä (**tehty**)
- käyttäjä voi poistaa aiemmin lisäämiään yhtälöitä (**tehty**)
- käyttäjä voi muokata aiemmin lisäämiään yhtälöitä (**tehty**)
- yhtälöt voivat sisältää muuttujan `t`, mikä kuvastaa aikaa (**tehty**)
- muuttujan `t` arvo kasvaa monotonisesti ja alkaa aina suoritusyrityksen alkaessa nollasta (**tehty**)
- yhtälöitä voi rajata esim. välille `1 <= x <= 3` lisäämällä yhtälön perään `| (rajaus)`. Esimerkki: `y = sin(x) | 0 <= x <= 2*PI` (**tehty**)
- käyttäjä voi hävitä pelin a) pallon tippuessa pois pelialueelta tai b) törmäämällä esteeseen (**tehty**)
- käyttäjä voi kenttiä ratkaisemalla "avata" uusia kenttiä pelattavaksi
- käyttäjä näkee kenttävalikkonäkymästä kenttäkohtaisen suorituksensa, mikä arvioidaan tähdillä (0-3 tähteä), joiden määrä lasketaan ensisijaisesti käytettyjen yhtälöiden määrästä (täydet kolme saa vain yhdellä yhtälöllä) ja toissijaisesti pallon kulkemisajan perusteella.
- sovellus sisältää vähintään 5 kenttää (4/5 tehty)
- sovellus tallentaa käyttäjän edistymisen sovellusbinäärin kanssa samaan kansioon binääritiedostoon "rollingballdata.dat"
- sovellus lataa `rollingballdata.dat` tiedoston sisällön käynnistyessä

## Jatkokehitysideoita

Peliä voisi jatkokehittää mm. seuraavasti:

- kun pelaaja onnistuneesti ratkaisee kentän, voisi ratkaisuun käytetyt yhtälöt ja saadut pisteet lähettää esim. Google Sheetsiin.
- peli voisi Google Sheetsistä kentän ratkaistua pelaajan halutessa hakea muiden ratkaisut kenttään, jotta tuloksia ja ratkaisuja voisi vertailla
- nämä tulokset voisi järjestää lähetysajan tai ratkaisun pistemäärän perusteella
- kenttiin voisi lisätä elementtejä kuten trampoliineja, nopeustehosteita (eng. speed boost) ja suunnanmuuttajia (kääntää pallon pyörimissuunnan)
- peli voisi hyväksyä myös parametrisiä yhtälöitä
- peliin voisi lisätä myös kenttäeditorin, jonka avulla voisi luoda ja jakaa omia kenttiä muille pelattavaksi
- peliin voisi lisätä musiikkia ja ääniefektejä
- lisää kenttiä!
