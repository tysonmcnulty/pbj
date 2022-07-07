grammar Molly;

file                     : (
                             heading
                             | relation_declaration
                             | term_declaration
                             | NEWLINE
                         )* ;

heading                  : HASH+ WORD* NEWLINE ;
relation_declaration     : DASH relation NEWLINE ;
term_declaration         : DASH term NEWLINE ;
relation                 : composition
                         | definition
                         | categorization ;
composition              : term COMPOSER (term LIST_DELIMITER)* term ;
definition               : term DEFINER (value LIST_DELIMITER)* value ;
categorization           : term CATEGORIZER (category LIST_DELIMITER)* category ;

term                     : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;
value                    : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;
category                 : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;


WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') (WHITESPACE | NEWLINE) -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
LIST_DELIMITER      : ('or' | (',' 'or'?)) ;
COMPOSER            : 'has'
                    | 'has many' ;
DEFINER             : 'is just'
                    | 'is either' ;
CATEGORIZER         : 'is a kind of'
                    | 'is a type of' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
