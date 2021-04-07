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
              (cons x (loop (read input-port)))
             )
            empty
         )
       )
     )
   )
)

; subset of given list
; reference : https://gist.github.com/vishesh/5731608
(define (subsets s)
  (if (null? s)
      (list null)
      (let ((rest (subsets (cdr s))))
        (append rest (map (lambda (x)
                            (cons (car s) x))
                          rest)))))

; process the list inside the list of subsets
; assuming sList is not null
(define (process-sublist sList)
  ( if(null? (cdr sList))
      (get-last (car sList))
      (+ (get-last (car sList)) (process-sublist (cdr sList) ))
   )
)

; process de subsets of items and return the list of the optimal weight calculated for
; each subset
(define (process-subsets subs)
  (cond
    [(null? (car subs)) (cons -1 (process-subsets (cdr subs) ))]
    [(null? (cdr subs))(cons (process-sublist (car subs)) empty)]
    [ else (cons (process-sublist (car subs)) (process-subsets (cdr subs))) ]
   )
)

; list without #\space #\newline #\return
; remv* : https://docs.racket-lang.org/reference/pairs.html

(define (get-filtered-content filename)
   (filter-symbol (remv* '(#\return #\space #\newline)(get-content filename)))
)

; change all symbols in a list to string
(define (filter-symbol L)
  ( cond
     [(null? L) '()]
     [(symbol? (car L)) (cons (symbol->string(car L)) (filter-symbol (cdr L)))]
     [else (cons (car L) (filter-symbol (cdr L))) ]
   )
)

; get maximum of a list
; ref: https://stackoverflow.com/questions/27128960/getting-the-largest-number-in-a-list-in-scheme
(define (maximum L)
     (if (null? (cdr L)) 
         (car L) 
         (if (< (car L) (maximum (cdr L)))  
             (maximum (cdr L)) 
             (car L)
         )
    )
)

; get index of element in a list
; ref : https://stackoverflow.com/questions/13562200/find-the-index-of-element-in-list
(define (get-list-index l el)
    (if (null? l)
        -1
        (if (= (car l) el)
            0
            (let ((result (get-list-index (cdr l) el)))
                (if (= result -1)
                    -1
                    (+ 1 result))))))


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

; keep the weights that are lower to the maximum capacity
(define (get-legal-knapsack wList max_cap)
  (begin
    ; (display "\n")  
    ; (display wList) ; debug purpose
    ; (display "\n")
    (cond
      [(and (< (car wList) 0) (not(null? (cdr wList)))) (cons 0 (get-legal-knapsack (cdr wList) max_cap))]
      [(and (< (car wList) 0) (null? (cdr wList))) (cons 0 empty)]
      [( and (>= (car wList) 0) (<= (car wList) max_cap) (null? (cdr wList)) ) (cons (car wList) empty)]
      [( and (>= (car wList) 0) (<= (car wList) max_cap) (not(null? (cdr wList)))) (cons  (car wList) (get-legal-knapsack (cdr wList) max_cap))]
      [( and (>= (car wList) 0) (>= (car wList) max_cap) (not(null? (cdr wList)))) (cons  0 (get-legal-knapsack (cdr wList) max_cap))]
      [ else (cons 0 empty) ]
     )
  )
 )

; process items
; return a list of items in the items = ((name, value,weight),...)
; assumant que la taille de la liste items est un multiple 3
(define (process-items items)
  (if( = (length (cdr(cdr(cdr items))) ) 0)
      (cons (list (car items) (car (cdr items)) (car (cdr (cdr items)))) empty)
     (
      cons(
            list (car items) (car (cdr items)) (car (cdr (cdr items)))
           ) (process-items (cdr(cdr(cdr items))) )
     )
  )
)

; get the names of items inside the list of items
; sum the values inside the list of items
; the subset always have 3 items
; item name, item value, item weight
; the index of the item value is therefore 1
; assuming the optimal_set is never null
(define (sums_values optimal_set)
  (cond
    [(null? (cdr optimal_set)) (list-ref (car optimal_set) 1)]
    [else (+ (list-ref (car optimal_set) 1) (sums_values (cdr optimal_set)) ) ]
  )
)

; get items names in the optimal subset
; assuming the optimal_set is never null
(define (get_items_names optimal_set)
  (cond
     [(null? (cdr optimal_set)) (cons (list-ref (car optimal_set) 0) empty)] ; item name is always the first element of the subset
     [else (cons (list-ref (car optimal_set) 0) (get_items_names (cdr optimal_set)) ) ]
   )
)

; process optimal solution
; put the optimal value at the beginning of the list
; and the items names as a sublist
(define (process_optimal_solution optimal_set)
  (cons (sums_values optimal_set) (cons (get_items_names optimal_set) empty) )
)


; knapsack
(define (knapsack capacity items)
  (let (
         ; get the list of the possible subsets
         (all_subsets (subsets items))
         ; get the list of legal weights among all the possible solution
         (legal_weights (get-legal-knapsack (process-subsets (subsets items) ) capacity))
        )
      (begin
        ;(display "\n")  
        ;(display legal_weights) ; debug purpose
        ;(display "\n")
        ;(display all_subsets)   ; debug purpose
        ;(display "\n")
        (let(
             ; get index of the maximum legal weight found in the legal_weights  list
             (max_index (get-list-index legal_weights (maximum legal_weights)))
             )
           (begin
             ;;(display "\n")
             ; (display max_index)
             (display "\n")
             (let (
                   ; get the index of the optimal subset
                   (optimal_subset (list-ref all_subsets max_index))
                   )
                (display "> Optimal subset found :")
                (display optimal_subset)
                (display "\n")
                (display "> Optimal solution : ")
                (display (process_optimal_solution optimal_subset))
                (display "\n")
                (display "\n")
                (process_optimal_solution optimal_subset)
             )
           )
        )
      )
   )
 )


; process-content
; content is a list of the data collected from the file
(define (process-content content)
  (
   let (
        (capacity (get-last content))
        ; (unprocessed_items (remove-first (remove-last content)))
        (processed_items (process-items (remove-first (remove-last content)) ))
       )
    (begin
      (display "\n")
      (display "> Collected capacity : ")
      (display capacity)
      (display "\n")
      ; (display unprocessed_items)
      (display "> Collected items : ")
      (display processed_items)
      (display "\n")
      (knapsack capacity processed_items)
     )
  )
)


; solveKnapsack
(define (solveKnapsack filename)
    ; get and process the content of the filename
    (process-content (get-filtered-content filename))
)



