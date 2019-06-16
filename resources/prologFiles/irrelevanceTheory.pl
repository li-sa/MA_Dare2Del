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
    file_name_extension(_N1, E, F1),
    file_name_extension(_N2, E, F2).

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

subtract_new(A, B, R) :-
    M is round(A),
    S is round(B),
    R is M - S.

older_than_one_year(F) :-
    change_time(F, T),
    get_current_time(C),
    subtract_new(C, T, R),
    R > 31536000.

empty(F) :-
    file(F),
    size_file(F, S),
    S = 0.

named_with_obsolete_identifier(F) :-
    path(FN, F),
    string_lower(FN, FNL),
    sub_string(FNL, _B, _L, _A, 'old').

named_with_obsolete_identifier(F) :-
    path(FN, F),
    string_lower(FN, FNL),
    sub_string(FNL, _B, _L, _A, 'temp').

irrelevant(F) :-
    empty(F).

irrelevant(F) :-
    named_with_obsolete_identifier(F).

irrelevant(F) :-
    older_than_one_year(F).

irrelevant(F) :-
    in_same_directory(F, Y),
    same_media_type(F, Y, _E),
    greater_or_equal(F, Y),
    later_or_equal_created(F, Y),
    later_or_equal_changed(F, Y),
    very_similar(F, Y),
    F \= Y.

irrelevant_secondfile(Y) :-
    in_same_directory(F, Y),
    same_media_type(F, Y, _E),
    greater_or_equal(F, Y),
    later_or_equal_created(F, Y),
    later_or_equal_changed(F, Y),
    very_similar(F, Y),
    F \= Y.

irrelevant_files(F, Set) :-
    setof(Body, (clause(irrelevant(F), Body), call(Body)), Set).

body_list(true)  --> [].

body_list((A,B)) -->
        body_list(A),
        body_list(B).

body_list(G) -->
        { G \= true },
        { G \= (_,_) },
        [G].

clause_body_list(G, Result) :-
    clause(G, Body),
    phrase(body_list(Body), Result),
    length(Result, Size),
    Size > 1.

nearmiss_files(F, Set) :-
    clause_body_list(irrelevant(F), Body),
    iterate_through_bodylist(Body, Body, Set).

iterate_through_bodylist(List, [H|T], Set) :-
    length([H|T], LengthHT),
    LengthHT > 1,
    Elem_neg = (\+ H),
    delete(List, H, LWH),
    append(LWH, [Elem_neg], NewList),
    find_nearmisses(NewList, Set),
    iterate_through_bodylist(List, T, X),
    Set = X.

iterate_through_bodylist(_List, [], _Set).

iterate_through_bodylist(_List, [_A], _Set).

find_nearmisses(BodyList, Set) :-
    body_list_to_callable(BodyList, Body_nearmiss),
    setof(Body_nearmiss, call(Body_nearmiss), Set).

find_nearmisses(_BodyList, _Set).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 2,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    BodyCallable = (Elem1, Elem2).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 3,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    BodyCallable = (Elem1, Elem2, Elem3).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 4,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 5,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 6,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    nth0(5, BodyList, Elem6),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5, Elem6).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 7,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    nth0(5, BodyList, Elem6),
    nth0(6, BodyList, Elem7),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5, Elem6, Elem7).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 8,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    nth0(5, BodyList, Elem6),
    nth0(6, BodyList, Elem7),
    nth0(7, BodyList, Elem8),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5, Elem6, Elem7, Elem8).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 9,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    nth0(5, BodyList, Elem6),
    nth0(6, BodyList, Elem7),
    nth0(7, BodyList, Elem8),
    nth0(8, BodyList, Elem9),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5, Elem6, Elem7, Elem8, Elem9).

body_list_to_callable(BodyList, BodyCallable) :-
    length(BodyList, BLL),
    BLL = 10,
    nth0(0, BodyList, Elem1),
    nth0(1, BodyList, Elem2),
    nth0(2, BodyList, Elem3),
    nth0(3, BodyList, Elem4),
    nth0(4, BodyList, Elem5),
    nth0(5, BodyList, Elem6),
    nth0(6, BodyList, Elem7),
    nth0(7, BodyList, Elem8),
    nth0(8, BodyList, Elem9),
    nth0(9, BodyList, Elem10),
    BodyCallable = (Elem1, Elem2, Elem3, Elem4, Elem5, Elem6, Elem7, Elem8, Elem9, Elem10).