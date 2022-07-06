grammar Molly;

file                     : (
                             heading
                             | relation_declaration
                             | definition_declaration
                             | term_declaration
                             | NEWLINE
                         )* ;

heading                  : HASH+ WORD* NEWLINE ;
relation_declaration     : DASH term RELATER (term LIST_DELIMITER)* term NEWLINE ;
definition_declaration   : DASH term DEFINER (value LIST_DELIMITER)* value NEWLINE ;
term_declaration         : DASH term NEWLINE ;
term                     : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;
value                    : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;

WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') (WHITESPACE | NEWLINE) -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
LIST_DELIMITER      : ('or' | (',' 'or'?)) ;
RELATER             : 'has'
                    | 'has many'
                    | 'is a kind of' ;
DEFINER             : 'is just'
                    | 'is either' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
