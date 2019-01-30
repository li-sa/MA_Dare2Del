in_same_directory(F1, F2) :-
    in_directory(F1, D),
    in_directory(F2, D).

same_file_extension(F1, F2, E) :-
    file_name_extension(N1, E1, F1),
    file_name_extension(N2, E2, F2),
    E1 = E2.

greater_than(F1, F2) :-
    path(F1, P1),
    path(F2, P2),
    size_file(P1, S1),
    size_file(P2, S2),
    S1 > S2.

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

irrelevant(F) :-
    in_same_directory(F, X),
    same_file_extension(F, X, E),
    greater_than(X, F),
    earlier_created_than(F, X),
    filename_similarity(F, X, D1), D1 > 0.90,
    filecontent_similarity(F, X, D2), D2 > 0.7.