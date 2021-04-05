#lang scheme

; Student : Benjamin Kataliko Viranga
; Student ID : 8842942
; CSI2520
; Projet Intégrateur -  Partie Fonctionnelle (Scheme)

;; Read a text file
; https://stackoverflow.com/questions/4181355/file-i-o-operations-scheme ;
; --- ;

(define (solveKnapsack filename)
  (call-with-input-file filename
    (lambda (input-port)
      (let loop ((x (read-char input-port)))
        (if (not (eof-object? x))
            (begin
              (display x)
              (loop (read-char input-port)))
            ""
         )
       )
     )
   )
)

