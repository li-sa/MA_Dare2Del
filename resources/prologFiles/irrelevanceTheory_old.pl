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

empty(F) :-
    file(F),
    size_file(F, S),
    S = 0.

named_with_obsolete_identifier(F) :-
    path(FN, F),
    string_lower(FN, FNL),
    sub_string(FNL, B, L, A, 'old').

named_with_obsolete_identifier(F) :-
    path(FN, F),
    string_lower(FN, FNL),
    sub_string(FNL, B, L, A, 'temp').

irrelevant(F) :-
    empty(F).

irrelevant(F) :-
    named_with_obsolete_identifier(F).

irrelevant(F) :-
    older_than_one_year(F).

irrelevant(F) :-
    in_same_directory(F, Y),
    same_media_type(F, Y, E),
    greater_or_equal(F, Y),
    later_or_equal_created(F, Y),
    later_or_equal_changed(F, Y),
    very_similar(F, Y),
    F \= Y.

relevant(F) :-
    in_same_directory(F, X),
    same_media_type(F, X, E),
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

set_of_clause(C, Set) :-
    setof(Body, (clause(C, Body), call(Body)), Set).

clause_body_list(Clause, Body) :-
    clause(Clause, Elements),
    clause_body_list_aux(Elements, Body).

clause_body_list_aux(Elements, [BodyPart|BodyRest]) :-
    Elements =.. [_, E | T],
    (   T = []
    ->  BodyPart = E,

        BodyRest = []
    ;   [ClauseRest] = T,
        true(BodyPart) = E,
        clause_body_list_aux(ClauseRest, BodyRest)
    ).

replace_substring(String, To_Replace, Replace_With, Result) :-
    (    append([Front, To_Replace, Back], String)
    ->   append([Front, Replace_With, Back], R),
         replace_substring(Back, To_Replace, Replace_with, Result)
    ;    Result = String
    ).

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

relevant_test(C, Set) :-
    clause_body_list(C, Body),
    length(Body, BodyLength),
    Max is BodyLength - 2,
    foreach(between(0, Max, X), iterate_body(Body, X, Z)).

iterate_body(Body, Counter, EachBody) :-
    nth0(Counter, Body, Elem, Rest),
    length(Rest, RestLength),
    term_to_atom(Elem, Elem_atom),
    string_concat(\+, Elem_atom, Elem_neg),
    nth0(RestLength, Body_neg, Elem_neg, Rest),
    write("Body_neg-"),
    write(Body_neg),
    atomic_list_concat(Body_neg, Body_neg_atoms),
    write("Body_neg_atoms-"),
    write(Body_neg_atoms),
    setof(Body_neg, call(Body_neg), Set),
    write("Set-"),
    write(Set).