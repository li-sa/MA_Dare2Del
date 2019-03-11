not_in_same_directory(F1, F2) :-
    in_directory(F1, D1),
    in_directory(F2, D2),
    D1 \= D2.

in_same_directory(F1, F2) :-
    in_directory(F1, D1),
    in_directory(F2, D2),
    D1 = D2.

same_media_type(P1, P2, E) :-
    path(F1, P1),
    path(F2, P2),
    file_name_extension(N1, E, F1),
    file_name_extension(N2, E, F2).

greater_or_equal(P1, P2) :-
    size_file(P1, S1),
    size_file(P2, S2),
    S2 >= S1.

later_or_equal_created(F1, F2) :-
    creation_time(F1, T1),
    creation_time(F2, T2),
    T2 >= T1.

later_or_equal_changed(F1, F2) :-
    change_time(F1, T1),
    change_time(F2, T2),
    T2 >= T1.

later_or_equal_accessed(F1, F2) :-
    access_time(F1, T1),
    access_time(F2, T2),
    T2 >= T1.

file_name_similarity(P1, P2, D) :-
    path(F1, P1),
    path(F2, P2),
    file_name_extension(N1, E1, F1),
    file_name_extension(N2, E2, F2),
    E1 = E2,
    isub(N1, N2, true, D).

file_content_similarity(F1, F2, D) :-
    open(F1, read, Stream1),
    open(F2, read, Stream2),
    read_string(Stream1, _, S1),
    read_string(Stream2, _, S2),
    isub(S1, S2, true, D).

very_similar(F1, F2) :-
    F1 \= F2,
    file_name_similarity(F1, F2, D1),
    D1 > 0.8,
    file_content_similarity(F1, F2, D2),
    D2 > 0.7.

get_current_time(C) :-
    get_time(X),
    C is round(X).

subtract(A, B, R) :-
    M is round(A),
    S is round(B),
    R is M - S.

older_than_one_year(F) :-
    change_time(F, T),
    get_current_time(C),
    subtract(C, T, R),
    R > 31536000.

irrelevant(F) :-
    older_than_one_year(F).

irrelevant(F) :-
    in_same_directory(F, Y),
    same_media_type(F, Y, EY),
    greater_or_equal(F, Y),
    later_or_equal_created(F, Y),
    later_or_equal_changed(F, Y),
    very_similar(F, Y).

relevant(F) :-
    in_same_directory(F, X),
    same_media_type(F, X, EX),
    greater_or_equal(F, X),
    later_or_equal_created(F, X),
    later_or_equal_changed(F, X),
    \+ very_similar(F, X),
    \+ irrelevant(F),
    F \= X.

relevant(F) :-
    in_same_directory(F, X),
    same_media_type(F, X, E),
    greater_or_equal(F, X),
    later_or_equal_created(F, X),
    very_similar(F, X),
    \+ later_or_equal_changed(F, X),
    \+ irrelevant(F),
    F \= X.

relevant(F) :-
    in_same_directory(F, X),
    same_media_type(F, X, E),
    greater_or_equal(F, X),
    very_similar(F, X),
    later_or_equal_changed(F, X),
    \+ later_or_equal_created(F, X),
    \+ irrelevant(F),
    F \= X.

relevant(F) :-
    in_same_directory(F, X),
    same_media_type(F, X, E),
    very_similar(F, X),
    later_or_equal_changed(F, X),
    later_or_equal_created(F, X),
    \+ greater_or_equal(F, X),
    \+ irrelevant(F),
    F \= X.

relevant(F) :-
    in_same_directory(F, X),
    very_similar(F, X),
    later_or_equal_changed(F, X),
    later_or_equal_created(F, X),
    greater_or_equal(F, X),
    \+ same_media_type(F, X, E),
    \+ irrelevant(F),
    F \= X.

relevant(F) :-
    very_similar(F, X),
    later_or_equal_changed(F, X),
    later_or_equal_created(F, X),
    greater_or_equal(F, X),
    same_media_type(F, X, E),
    \+ in_same_directory(F, X),
    \+ irrelevant(F),
    F \= X.

neg(C) :-
    C, !, fail.

set_of_clause(C, Set) :-
    setof(Body, (clause(C, Body), call(Body)), Set).