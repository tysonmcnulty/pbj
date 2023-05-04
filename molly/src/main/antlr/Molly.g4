grammar Molly;

file                     : (
                             MARKDOWN_COMMENT
                             | text
                             | relation_declaration
                             | term_declaration
                             | NEWLINE
                         )* EOF ;

text                     : ~DASH ~NEWLINE* NEWLINE ;
term_declaration         : DASH term NEWLINE ;
relation_declaration     : DASH relation (DELIMITER? subrelation)? NEWLINE ;

relation                 : categorization
                         | composition
                         | definition
                         | description ;

categorization           : mutant=relation_mutant operator=categorizer mutation=categorization_mutation ;
composition              : mutant=relation_mutant operator=composer MULTIPLIER? mutation=composition_mutation ;
definition               : mutant=relation_mutant operator=definer mutation=definition_mutation ;
description              : mutant=relation_mutant operator=describer mutation=description_mutation ;

subrelation              : subdefinition ;
subdefinition            : SUBORDINATOR operator=definer mutation=definition_mutation ;

categorizer              : IDENTITY_VERB ;
composer                 : (OBVIATOR? QUALIFIER? COMPOSER_VERB) | (QUALIFIER? OBVIATOR? COMPOSER_VERB) ;
definer                  : IDENTITY_VERB DEFINER ;
describer                : (OBVIATOR? QUALIFIER? IDENTITY_VERB)
                         | (QUALIFIER? OBVIATOR? IDENTITY_VERB )
                         | (IDENTITY_VERB OBVIATOR?) ;

relation_mutant          : unit ;

categorization_mutation  : CATEGORIZER category ;
composition_mutation     : CATEGORIZER category | unit ;
definition_mutation      : values | unit ;
description_mutation     : PREDELIMITER? descriptor (DELIMITER negation)? ;

category                 : unit ;
unit                     : INDEFINITE_ARTICLE? (context? term) | (term context?) ;
descriptor               : term ;
context                  : (term '\'s') | ('of' term);

negation                 : value ;
term                     : BOXED_WORDS | WORD+ ;
values                   : PREDELIMITER? (value DELIMITER)+ value ;
value                    : BOXED_WORDS | WORD+ ;


MARKDOWN_COMMENT    : '[//]: # (' .*? ')' NEWLINE -> skip ;
WHITESPACE          : (' ' | '\t') -> skip ;
DASH                : '-' ;
HASH                : '#' ;
BOXED_WORDS         : '"' (WORD WHITESPACE)* WORD '"' | '*' (WORD WHITESPACE)* WORD '*' ;
INDEFINITE_ARTICLE  : ('a' | 'A' | 'an' | 'An' | 'some' | 'Some' ) -> skip ;
IDENTITY_VERB       : 'is' | 'are' | 'be' ;
NEWLINE             : ('\r'? '\n' | '\r')+ ;
DELIMITER           : (', or' | ',' | 'or' ) ;
PREDELIMITER        : 'either'
                    | 'one of' ;
CATEGORIZER         : 'kind of' | 'kinds of'
                    | 'type of' | 'types of'
                    | 'sort of' | 'sorts of' ;
DEFINER             : 'just' ;
OBVIATOR            : 'evidently'
                    | 'apparently' ;
SUBORDINATOR        : 'which' ;
COMPOSER_VERB       : 'has'
                    | 'have' ;
MULTIPLIER          : 'many'
                    | 'several'
                    | 'zero or more'
                    | 'one or more'
                    | 'two'
                    | 'three' ;
QUALIFIER           : 'may' ;
WORD                : (LOWERCASE | UPPERCASE | DASH)+ ;

fragment LOWERCASE  : [a-z] ;
fragment UPPERCASE  : [A-Z] ;
