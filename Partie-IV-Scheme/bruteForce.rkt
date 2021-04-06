#lang scheme

; Student : Benjamin Kataliko Viranga
; Student ID : 8842942
; CSI2520
; Projet Intégrateur -  Partie Fonctionnelle (Scheme)

; Read a text file
; https://stackoverflow.com/questions/4181355/file-i-o-operations-scheme ;
; --- ;
; - https://www.scheme.com/tspl3/io.html - ;
; Prendre le contenu du fichier 
(define (get-content filename)
  (call-with-input-file filename
    (lambda (input-port)
      (let loop ((x (read input-port)))
        (if (not (eof-object? x))
            (begin
              ; (display x)
              ; (loop (read-char input-port))
              (cons x (loop (read input-port)))
             )
            empty
         )
       )
     )
   )
)

; list without #\space #\newline #\return
; remv* : https://docs.racket-lang.org/reference/pairs.html

(define (get-filtered-content filename)
   (filter-symbol (remv* '(#\return #\space #\newline)(get-content filename)))
)

; change all symbole to a list to string
(define (filter-symbol L)
  ( cond
     [(null? L) '()]
     [(symbol? (car L)) (cons (symbol->string(car L)) (filter-symbol (cdr L)))]
     [else (cons (car L) (filter-symbol (cdr L))) ]
   )
)



; remove first element of a list
(define (remove-first elems)
  (cdr elems)
)

; https://stackoverflow.com/questions/13175152/scheme-getting-last-element-in-list/51202247
; find last element of a list in scheme
;
(define (get-last elems)
   (car (reverse elems))
)

; remove last element of a list
(define (remove-last elems)
  (if(= (length elems) 1)
     '()
     (cons (car elems) (remove-last (cdr elems)))
   )
)

; process items
; return a list of items in the items = ((name, value,weight),...)
; assumant que la taille de la liste items est un multiple 3
(define (process-items items)
  (if( = (length (cdr(cdr(cdr items))) ) 0)
      (list (car items) (car (cdr items)) (car (cdr (cdr items))))
     (
      cons(
            list (car items) (car (cdr items)) (car (cdr (cdr items)))
           ) (process-items (cdr(cdr(cdr items))) )
     )
  )
)


; knapsack
(define (knapsack capacity items)
  (#t)
 )


; process-content
; content is a list of chars
(define (process-content content)
  (
   let (
        (capacity (get-last content))
        (unprocessed_items (remove-first (remove-last content)))
        (processed_items (process-items (remove-first (remove-last content)) ))
       )
    (begin
      (display capacity)
      (display "\n")
      (display unprocessed_items)
      (display "\n")
      (display processed_items)
      processed_items
     )
  )
)


; solveKnapsack
(define (solveKnapsack filename)
    ; get filtered content
    (process-content (get-filtered-content filename))
)



