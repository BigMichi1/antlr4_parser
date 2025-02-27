grammar TreeSelector;

// Parser rules
selector
    : '/' (nodeName | wildcard) attributeSelector? (nodeSelector)* EOF
    ;

nodeSelector
    : '/' (nodeName | wildcard) attributeSelector?
    ;

nodeName
    : IDENTIFIER
    ;

wildcard
    : '*'
    ;

attributeSelector
    : '{' attributeExpr '}'
    ;

attributeExpr
    : attributeName '=' attributeValue
    ;

attributeName
    : IDENTIFIER
    | STRING
    ;

attributeValue
    : IDENTIFIER
    | STRING
    | VERSION
    ;

// Lexer rules
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

VERSION
    : [0-9]+ ('.' [0-9]+)* // Matches version numbers like 1.0.0, 2.1, etc.
    ;

STRING
    : '\'' (~['\\] | '\\' .)* '\''
    ;

WS
    : [ \t\r\n]+ -> skip
    ;