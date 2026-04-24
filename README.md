# 📚 Adlis Bibliotekssystem
Ett backend-system för bibliotekshantering utvecklat i Java. Projektet är byggt med en strikt **trelagersarkitektur** och använder **JDBC** för kommunikation med en MySQL-databas. Projektet är en del av kursen *Objektorienterad analys och design*.

## 🏗️ Projektstruktur
Istället för att dela in koden efter lager (alla controllers i en mapp, alla services i en), använder projektet en **Domändriven struktur (Feature-based packaging)**. Det betyder att allt som rör en specifik funktion ligger samlat, vilket gör koden mycket lättare att navigera och underhålla.

```text
AdlisBibliotek/
 ├── src/
 │    ├── author/        # Hantering av författare och databaskopplingar
 │    ├── book/          # BookController, BookService, BookRepository, DTOs
 │    ├── category/      # Hantering av bokkategorier och statistik
 │    ├── db/            # DatabaseConnection (Laddar inloggningsuppgifter)
 │    ├── loan/          # Låne-logik, datumhantering och förseningsstatus
 │    ├── member/        # MemberController, MemberService, MemberRepository m.m.
 │    └── Main.java      # Startpunkt för applikationen med Huvudmenyn
 ├── .gitignore                   # Döljer känsliga filer från GitHub
 ├── database.properties.template # Mall för databasuppkoppling
 └── README.md                    # Denna dokumentation
```

## 🧠 Arkitektur och Designval

### Trelagersarkitektur
Systemet är strikt uppdelat för att uppnå hög *Separation of Concerns*:
1. **Controller-lagret:** Hanterar all interaktion med användaren (konsolen). Skriver ut menyer och läser in input via `Scanner`. Controllern pratar *endast* med Service-lagret.
2. **Service-lagret:** Innehåller all affärslogik. Här kontrolleras säkerhetsspärrar (t.ex. *man kan inte sänka antalet böcker till färre än vad som är utlånat*). 
3. **Repository-lagret:** Sköter all SQL och databaskommunikation.

### Smarta DTO:er (Data Transfer Objects)
Istället för att skicka hela databasentiteter hela vägen ut till användaren används DTO:er. 
* Detta gör att vi kan skräddarsy exakt vilken data som ska visas.
* Exempel: I `CategoryRepository` räknar en avancerad SQL-fråga (`LEFT JOIN`) ut hur många böcker som finns i varje kategori och returnerar detta direkt i en `CategoryDTO`.

### Hantering av komplexa relationer (Many-to-Many)
Systemet hanterar avancerade databasrelationer, specifikt mellan böcker, författare och kategorier. Eftersom en bok kan ha flera författare och tillhöra flera kategorier används separata kopplingstabeller i databasen. Funktioner finns implementerade för att lägga till och ta bort dessa kopplingar dynamiskt.

### Användarvänlighet (UX i konsolen)
Ett medvetet designval har varit att göra konsol-gränssnittet förlåtande. När användaren till exempel ska koppla en bok till en kategori eller författare, hämtar systemet först en lista på alla tillgängliga alternativ och skriver ut dem på skärmen. På så sätt slipper användaren gissa vilket ID-nummer som tillhör vem.

### Databasanslutning
Istället för att hårdkoda databasuppgifter i varje repository, används en centraliserad `DatabaseConnection`-klass i ett eget package (`db`). Detta gör systemet mycket lättare att underhålla om lösenord eller databas-URL behöver ändras.

## 🚀 Kom igång och Användning

**Tekniska krav:**
* Java 21+
* MySQL Databas

**Installation:**
1. Importera SQL-scriptet i MySQL för att skapa databasen `adlisbibliotek` och dess tabeller.
2. Kopiera filen `database.properties.template` och döp den nya filen till `database.properties`.
3. Öppna den nya `database.properties` och fyll i dina lokala inloggningsuppgifter till MySQL:
   ```properties
   db.url=jdbc:mysql://localhost:3306/adlisbibliotek
   db.user=root
   db.password=ditt_lösenord
4. Kör programmet via `Main.java`.

**Huvudmeny:**
Programmet navigeras via sifferinmatning:
```text
=== HUVUDMENY ===
1. Böcker
2. Medlemmar
3. Lån
4. Författare
5. Kategorier
0. Avsluta
```
