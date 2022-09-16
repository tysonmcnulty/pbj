grammar Molly;

file                     : (
                             heading
                             | relation_declaration
                             | term_declaration
                             | NEWLINE
                         )* ;

heading                  : HASH+ .*? NEWLINE ;
term_declaration         : DASH term NEWLINE ;
relation_declaration     : DASH relation (DELIMITER? subrelation)? NEWLINE ;

relation                 : categorization
                         | composition
                         | description
                         | enumeration ;
categorization           : term CATEGORIZER term ;
composition              : term composer term ;
description              : term DESCRIBER term (DELIMITER negation)? ;
enumeration              : term ENUMERATOR (value DELIMITER)* value ;

subrelation              : subcategorization
                         | subcomposition
                         | subdescription
                         | subenumeration ;
subcategorization        : SUBORDINATOR (CONTEXTUALIZER term)? CATEGORIZER term ;
subcomposition           : SUBORDINATOR (CONTEXTUALIZER term)? composer term ;
subdescription           : SUBORDINATOR (CONTEXTUALIZER term)? DESCRIBER term (DELIMITER negation)? ;
subenumeration           : SUBORDINATOR (CONTEXTUALIZER term)? ENUMERATOR (value DELIMITER)* value ;

composer                 : QUALIFIER? COMPOSER_VERB ;

term                     : INDEFINITE_ARTICLE? '*' term '*'
                         | INDEFINITE_ARTICLE? '"' term '"'
                         | WORD+ ;
negation                 : WORD+ ;
value                    : INDEFINITE_ARTICLE? '*' value '*'
                         | INDEFINITE_ARTICLE? '"' value '"'
                         | WORD+ ;


MARKDOWN_COMMENT    : NEWLINE '[comment]: <> (' .*? ')' -> skip ;
WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An') -> skip ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
DELIMITER           : (', or' | ',' | 'or' ) ;
CATEGORIZER         : 'is a kind of'
                    | 'is a type of'
                    | 'is just'
                    | 'are just' ;
DESCRIBER           : 'evidently is'
                    | 'evidently has' ;
ENUMERATOR          : 'can only be' ;
SUBORDINATOR        : 'which'
                    | 'wherein' ;
COMPOSER_VERB       : 'has some kind of'
                    | 'has many'
                    | 'has'
                    | 'have many'
                    | 'have' ;
QUALIFIER           : 'may'
                    | 'probably' ;
CONTEXTUALIZER      : 'its' ;
WORD                : (LOWERCASE | UPPERCASE)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
