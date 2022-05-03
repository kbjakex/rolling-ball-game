## Sovelluksen käyttöohje

Aloita lataamalla sovellus [täältä](https://github.com/kbjakex/ot-harjoitystyo/releases/tag/viikko6). 
Varmista, että [Java 18](https://jdk.java.net/18/) on asennettu ja toimii. Voit tarkistaa Java-versiosi ja Javan toimivuuden avaamalla komentokehotteen ja kirjoittamalla komennon `java --version`.

Avaa konsoli samaan kansioon, missä ladattu jar-tiedosto sijaitsee. Käynnistä sovellus komennolla
```console
java -jar RollingBall-1.0-SNAPSHOT.jar
```

### Sovelluksen toiminta

Sovellus on siis peli, ja avautuu päävalikkoon. 


<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/mainmenu.png" width="600">

Kirjautumista ei vaadita. Päävalikosta pääsee suoraan peliin "Start"-painikkeesta, jolloin peli alkaa viimeisestä kentästä, jonka olet avannut. 
Ensimmäisellä avauskerralla ainoastaan ensimmäinen kenttä on auki. 


Päävalikosta pääsee myös kenttävalikkoon "Level Select"-painikkeen avulla. Kenttävalikko näyttää seuraavalta:


<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/levels.png" width="600">

Kenttävalikon avulla voi palata jo läpipäästyihin kenttiin, jotta voi esimerkiksi yrittää parantaa suoritustaan, tai nähdä, kuinka ratkaisi kentän.
Läpi päästyjen kenttien yläpuolella tulee näkyviin 3 tähteä, jotka kertovat, kuinka hyvin suoritus meni: 0 on huonoin ja 3 täydellinen.

Joko kenttävalikon tai päävalikon kautta pääsee pelinäkymään, joka näyttää seuraavalta:
<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/game.png" width="600">

Pelinäkymässä on tarkoitus syöttää alaosan syöttökenttään yhtälöitä, kuten `2x/3 + 1`, jotka piirtyvät pelikentälle. Vasemmassa yläkulmassa olevalla vihreällä start-nappulalla
saa pelin käyntiin, jolloin pallo lähtee liikkeelle. Käyttäjän on tarkoitus syötteidensä avulla rakentaa pallolle reitti lipulla merkittyyn maaliin.

Annetut syötteet löytyvät syöttökentän alapuolelta listana, jossa niitä voi muokata tai poistaa. Syötteen väri on sama, kuin pelikentällä olevan sitä vastaavan
käyrän väri. Syöttökentän yläpuolelta ohutta palkkia voi vetää hiirellä ylöspäin, jotta näkee useamman syötteen kerrallaan:
<img src="https://raw.githubusercontent.com/kbjakex/ot-harjoitystyo/main/dokumentaatio/kuvat/inputs.png" width="600">

### Yhtälöiden syöttäminen

Yhtälöissä on käytössä kaksi muuttujaa: koordinaatti `x`, ja aika `t` (sekunteina). Omia muuttujia ei voi määritellä, eikä tällä hetkellä aiemmin määriteltyjä omia
funktioita voi kutsua.

Sovellus tunnistaa vakiot `pi` (3.141) ja `e` (2.718).

Käytettävissä olevia matemaattisia operaattoreita ovat `+`, `-`, `*`, `/` (jakolasku) ja `^` (potenssi).

Lisäksi syötteissä voi käyttää seuraavia funktioita:
- `sin()`, `cos()`, `tan()`, 
- `asin()`, `acos()`, `atan()`, `atan2(y,x)` tai `arcsin()`, `arccos()`, `arctan()`
- `sinh()`, `cosh()`, `tanh()`
- `exp()`, `sqrt()`, `cbrt()`, `hypot(x,y)`, `pow(b, x)`, `log()` tai `ln()`, `log10()` tai `lg()`
- `abs()`, `min(a,b)`, `max(a,b)`, `sign()` tai `signum()`, `floor()`, `round()`, `ceil()`

Yhtälösyötteen oikealla puolella pienemmässä syötekentässä on "filter" eli rajaus. Tämän avulla voi rajata, milloin yhtälö piirretään.
Rajauksissa on käytössä kaikki samat muuttujat, operaattorit, vakiot ja funktiot, mutta lisäksi vertailuoperaattorit `<`, `<=`, `>`, `>=`, `==` ja `!=`.

Esimerkiksi seuraava rajaisi syötteen välille -5..5: `-5 <= x <= 5`, ja seuraava välille 0..inf: `x >= 0`

Rajausten käytöstä ei koskaan vähennetä pisteitä, sillä ne ovat hyödyllisiä lähinnä usesan yhtälön tilanteissa, jolloin pisteitä menee joka tapauksessa yhtälöiden määrästä.
