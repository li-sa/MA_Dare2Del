in_same_directory(F1, F2) :-
    in_directory(F1, D),
    in_directory(F2, D).

same_file_extension(F1, F2, E) :-
    file_name_extension(N1, E1, F1),
    file_name_extension(N2, E2, F2),
    E1 = E2.

greater_or_equal_than(F1, F2) :-
    path(F1, P1),
    path(F2, P2),
    size_file(P1, S1),
    size_file(P2, S2),
    S1 >= S2.

earlier_created_than(F1, F2) :-
    creation_time(F1, T1),
    creation_time(F2, T2),
    T1 < T2.

earlier_changed_than(F1, F2) :-
    change_time(F1, T1),
    change_time(F2, T2),
    T1 < T2.

earlier_accessed_than(F1, F2) :-
    access_time(F1, T1),
    access_time(F2, T2),
    T1 < T2.

filename_similarity(F1, F2, D) :-
    file_name_extension(N1, E1, F1),
    file_name_extension(N2, E2, F2),
    isub(N1, N2, true, D).

filecontent_similarity(F1, F2, D) :-
    path(F1, P1),
    path(F2, P2),
    open(P1, read, Stream1),
    open(P2, read, Stream2),
    read_string(Stream1, _, S1),
    read_string(Stream2, _, S2),
    isub(S1, S2, true, D).

very_similar(F1, F2) :-
    filename_similarity(F1, F2, D1), D1 > 0.8,
    filecontent_similarity(F1, F2, D2), D2 > 0.7.

get_current_time(C) :-
    get_time(X),
    C is round(X).

subtract(A, B, R) :-
    M is round(A),
    S is round(B),
    R is M - S.

irrelevant_according_to_accessing_time(F) :-
    access_time(F, T),
    get_current_time(C),
    subtract(C, T, R),
    R > 86400.

irrelevant_compared_to_other_file(F) :-
    in_same_directory(F, X),
    same_file_extension(F, X, E),
    greater_or_equal_than(X, F),
    earlier_created_than(F, X),
    very_similar(F, X).