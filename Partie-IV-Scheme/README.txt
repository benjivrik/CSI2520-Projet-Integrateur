; Student : Benjamin Kataliko Viranga
; Student ID : 8842942
; CSI2520
; Projet Intégrateur -  Partie Fonctionnelle (Scheme)

How to run the program ?

Vous pouvez load le fichier bruteForce.rkt dans le
logiciel DrRacket. 

Ce fichier est codé en Scheme. ( avec '#lang scheme' au début du fichier.)

Le fichier p1.txt contient les données du Knapsack.

La fonction principale peut être lancée avec 

> (solveKnapsack filename) 

où filename est le fichier à parse, soit p1.txt pour ce cas. 

La fonction (Knapsack capacity items) est également définie. Ainsi si vous décidez de run la commande
ci-dessous à partir du terminal, vous obtiendrez les résultats correspondant au Knapsack.

>  (knapsack 7 '(("A" 1 1) ("B" 6 2) ("C" 10 3) ("D" 15 5))) 


La solution obtenue après avoir utilisé la fonction solveKnapsack est disponible dans 
le fichier filename avec une extension .sol 
