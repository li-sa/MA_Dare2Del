is_file(X) :- file(X).
in_same_directory(X, Y) :- in_directory(X, D), in_directory(Y, D).
larger(X, Y) :- size(X, S1), size(Y, S2), S1 > S2.
newer(X, Y) :- creation_time(X, T1), creation_time(Y, T2), T1 < T2.

