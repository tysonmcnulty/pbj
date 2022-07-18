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
relation                 : categorization
                         | composition
                         | definition
                         | description ;
categorization           : term CATEGORIZER (category LIST_DELIMITER)* category ;
composition              : term COMPOSER (term LIST_DELIMITER)* term ;
definition               : term DEFINER representation ;
description              : term DESCRIBER (descriptor LIST_DELIMITER)* descriptor ;

term                     : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;
category                 : '*' term '*'
                         | '"' term '"'
                         | WORD+ ;
descriptor               : WORD+ ;
representation           : WORD+ ;


WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') (WHITESPACE | NEWLINE) -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
LIST_DELIMITER      : ('or' | (',' 'or'?)) ;
COMPOSER            : 'has'
                    | 'has many' ;
DEFINER             : 'is just' ;
DESCRIBER           : 'is evidently' ;
CATEGORIZER         : 'is a kind of'
                    | 'is a type of' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
