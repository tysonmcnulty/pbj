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
                         | description
                         | enumeration ;
categorization           : term CATEGORIZER category ;
composition              : term COMPOSER (term DELIMITER)* term ;
description              : term DESCRIBER term (DELIMITER negation)? ;
enumeration              : term ENUMERATOR (value DELIMITER)* value ;

term                     : INDEFINITE_ARTICLE? '*' term '*'
                         | INDEFINITE_ARTICLE? '"' term '"'
                         | WORD+ ;
category                 : INDEFINITE_ARTICLE? '*' category '*'
                         | INDEFINITE_ARTICLE? '"' category '"'
                         | WORD+ ;
negation                 : WORD+ ;
value                    : '"' value '"'
                         | WORD+ ;


MARKDOWN_COMMENT    : NEWLINE '[comment]: <> (-' .*? ')' -> skip ;
WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
DELIMITER           : (', or' | ',' | 'or' ) ;
CATEGORIZER         : 'is a kind of'
                    | 'is a type of'
                    | 'is just' ;
DESCRIBER           : 'is evidently'
                    | 'evidently has' ;
ENUMERATOR          : 'can only be' ;
COMPOSER            : 'has'
                    | 'has many' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
