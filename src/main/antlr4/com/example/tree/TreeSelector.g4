grammar TreeSelector;

// Parser rules
multiSelector
    : selectorExpression ('|' selectorExpression)* EOF
    ;

selectorExpression
    : selector
    | deepSelector
    ;

selector
    : '/' (nodeName | wildcard | placeholder) attributeSelector? (nodeSelector)*
    ;

deepSelector
    : '**' '/' (nodeName | wildcard) attributeSelector? (nodeSelector)*
    ;

nodeSelector
    : '/' (nodeName | wildcard | placeholder) attributeSelector?
    ;

nodeName
    : IDENTIFIER
    ;

wildcard
    : '*'
    ;

placeholder
    : '~~'
    ;

attributeSelector
    : '{' attributeExpr (',' attributeExpr)* '}'
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
    | wildcard  // Allow wildcard as an attribute value
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