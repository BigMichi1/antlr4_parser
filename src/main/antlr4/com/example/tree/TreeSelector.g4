grammar TreeSelector;

// Parser rules
selector
    : '/' nodeName (nodeSelector)* EOF
    ;

nodeSelector
    : '/' (nodeName | wildcard)
    ;

nodeName
    : IDENTIFIER
    ;

wildcard
    : '*'
    ;

// Lexer rules
IDENTIFIER
    : [a-zA-Z0-9_]+
    ;

WS
    : [ \t\r\n]+ -> skip
    ;