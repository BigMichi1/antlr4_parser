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
    ;

// Lexer rules
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

STRING
    : '\'' (~['\\] | '\\' .)* '\''
    ;

WS
    : [ \t\r\n]+ -> skip
    ;