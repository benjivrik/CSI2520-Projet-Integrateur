%
%Student : Benjamin Kataliko Viranga
%Student ID : 8842942
%CSI2520
%Projet Intégrateur -  Partie Concurrente (Go)
%


% read a file in prolog
% reference : https://stackoverflow.com/questions/37573618/how-to-read-a-file-in-prolog
% reference : https://stackoverflow.com/questions/23411139/prolog-unexpected-end-of-file
% at_end_of_stream : https://www.swi-prolog.org/pldoc/man?predicate=at_end_of_stream/1
% at_end_of_stream succeeds when the last line is read.
read_file(Stream, Lines) :-
    read_line_to_codes(Stream, Codes),     % Attempt a read Line from the stream
    atom_chars(Line, Codes),
    (  at_end_of_stream(Stream)       % If we're at the end of the stream then...
    -> Lines = [Line]                 %  at_end_of_stream succeeds when the last line is read.
    ;  Lines = [Line|NewLines],       % Otherwise, Lines is Line followed by
       read_file(Stream, NewLines)    %   a read of the rest of the file
    ).

% Ce predicat retourne les donnees du fichier
get_data(Filename, Data):-
    open(Filename, read, Str),nl,
    write('Reading : '), writeln(Filename),
    read_file(Str, Data),          
    close(Str),
    %write(Data), nl, 
    process_data(Data,_,_,_).      %process data

%remove first element of list
%return the first element and the rest of the list
remove_first([F|L],F,LL):-
    append([],L,LL).
    

% truncate list and get the first N elements
% ref : https://stackoverflow.com/questions/27479915/how-to-trim-first-n-elements-from-in-list-in-prolog
% L - ToTruncate
% N - Desired Length
% R - Result
trim(L,N,R) :-     % to trim N elements from a list
   length(R,N),    % - generate an unbound prefix list of the desired length
   append(R,_,L). % - and use append/3 to get the desired suffix.


% no_space_str_to_int
% ensuring there is no space as well
% Str as input and output the Int
% Le Str doit etre en representation string
no_space_str_to_int(Str, Int):-
    split_string(Str, " ", " ", Str_split),  % remove any space from the string
    remove_first(Str_split, Str_new, _), % get the first elem without space
    atom_number(Str_new, Int).           % get the integer

% process data
% Le format de la liste Data lu fichier filename est 
% ['4 ', 'A  1  1', 'B  6  2', 'C  10 3', 'D  15 5', '7']
% Avec le premier element de la liste representant le nombre d'element à ajouter
% Cet élement est suivi des élements à ajouter
% le dernier élement de la liste est la capacité du sac
process_data(Data, _L_items_weight, _L_items_value, _All_items):-
    remove_first(Data, F, LL),           % LL is Data without the first element N_items
    no_space_str_to_int(F,N_items),      % get the integer value for the number of items
    % writeln(N_items), 
    last(LL, Capacity_str),             % get the capacity in string at the end of the list LL
    no_space_str_to_int(Capacity_str, Capacity), % get the Knapsack capacity
    % writeln(Capacity),
    trim(LL,N_items, LL_new),  % N_items is the number of element to be added in the knapsack
    % writeln(LL_new),  % list of items in str
    % process items
    process_items(LL_new, L_items_weight, L_items_value, All_items).


% get the items and initialize the corresponding list
get_items([],[],[],[]).
get_items([I|L],L_items_weight,L_items_value, All_items):-
       get_items(L, W, V, All),
       % knowing that I is a string
       split_string(I, " ", " ", I_list),
       % index 0 is the name of the item
       % index 1 is the value of the item
       % index 2 is the weight of the item
       length(I_list, 3), % ensure the size of the list is 3. 
       nth0(0, I_list, Item_name),  % Item name in string
       nth0(1, I_list, Item_value_str),
       nth0(2, I_list, Item_weight_str),
       % get item and weigth value in integer
       no_space_str_to_int(Item_value_str, Item_value),
       no_space_str_to_int(Item_weight_str, Item_weight),
       % initialize the item
       Item = item(Item_name,Item_value,Item_weight),
       % ajouter les termes composés pour les items dans la liste
       append([Item],All,All_items),
       % collecter les poids des items
       append([Item_weight], W,L_items_weight),
       % collecter les valeurs des items
       append([Item_value], V, L_items_value).

%retourne les items en termes composés dans la list L_items_list
%L_items_weight : liste des poids
%L_items_value  : liste des valeurs
%L_items_list   : Liste des items en termes composés
process_items(L_items_str, L_items_weight, L_items_value, All_items):-
    % get the corresponding items
    %writeln(L_items_str),
    get_items(L_items_str,L_items_weight,L_items_value,All_items),nl,
    write('> Collected Items : '), writeln(All_items),
    write('> Items weights : '),  writeln(L_items_weight),
    write('> Items values : '),  writeln(L_items_value),nl.



%dynamic_process
%dynamic_process(Capacity, L_items_weight, L_items_value,All_items, Value, L_items_list).
       

%solveKnapsack(Filename, Value, L_items_list).
%knapsack(Capacity, L_items_weight, L_items_value, Value, L_items_list).