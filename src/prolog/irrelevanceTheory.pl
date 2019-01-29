is_file(X) :- file(X).
in_same_directory(X, Y) :- in_directory(X, D), in_directory(Y, D).
greater_than(X, Y) :- size(X, S), size(Y, Z), S > Z.
same_mediatype(X, Y) :- media_type(X, A), media_type(Y, A).

earlier_created_than(X, Y) :- creation_time(X, T1), creation_time(Y, T2), T1 < T2.
earlier_changed_than(X, Y) :- change_time(X, T1), change_time(Y, T2), T1 < T2.
earlier_accessed_than(X, Y) :- access_time(X, T1), access_time(Y, T2), T1 < T2.

irrelevant(X) :- in_same_directory(X, Y), same_mediatype(X, Y),
    greater_than(Y, X), earlier_created_than(X, Y).