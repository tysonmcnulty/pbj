grammar Molly;

heading                  : HASH+ WORD* NEWLINE ;
relationship_declaration : DASH term RELATION (term LIST_DELIMITER)* term NEWLINE ;
term_declaration         : DASH term NEWLINE ;
term                     : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;

WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') (WHITESPACE | NEWLINE) -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
LIST_DELIMITER      : ('or' | (',' 'or'?)) ;
RELATION            : 'has'
                    | 'has many'
                    | 'is a kind of'
                    | 'is either' ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
