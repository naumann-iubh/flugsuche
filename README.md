# Flugsuche

Für Suchergebnisse in dieser App wurde auf die Skyscanner-Api zurückgegriffen.
Da es sich bei dieser App, um ein Studienprojekt innerhalb einer  Fallstudie handelt,
wurde darauf verzichtet eine kostenpflichtige Kooperation mit Skyscanner einzugehen.
Aus diesem Grund wird in dieser App ein Test-Api-Key verwendet, welcher nur eine begrenzte Anzahl von 
Anfragen zulässt.
Sollte es zu keinem Suchergebnis kommen, kann es an der Limitation des Keys liegen.
Aus diesem Grund wurde eine Dummy Methode implementiert, welche einen Original Response von Skyscanner einliest
und ein Ergebnis zurückliefert.
Diese Methode wird ausgeführt, wenn sie als Personananzahl 99 Personen angeben