grammar TreeSelector;

// TreeSelector Grammar
//
// A grammar for selecting nodes from a tree structure using various path expressions
// and attribute filters. This supports:
//
// - Basic path navigation: /Root/Child1/GrandChild1
// - Wildcards: /Root/[wildcard] (all children of Root)
// - Deep traversal: [deep][wildcard] (all nodes in the tree)
// - Placeholders: /Root/[placeholder] (all nodes under Root at any depth)
// - Relative paths: /Root/Child1/../Child2 (navigate to siblings via parent)
// - Attribute selectors: /Root/[wildcard]{type=component} (filter by attributes)
// - Multiple attribute filters: /Root/[wildcard]{type=component,variant=primary}
// - Multiple selectors: /Root/Child1|/Root/Child2 (combine results)
//
// where [wildcard] = *, [deep] = **, [placeholder] = ~~

// Parser rules

// Top-level rule that matches one or more selector expressions separated by pipe (|)
// Examples:
// - /Root/Child1
// - /Root/Child1|/Root/Child2
// - [deep]/[wildcard]{type=component}|/Root/Child1
multiSelector
    : selectorExpression ('|' selectorExpression)* EOF
    ;

// Wraps different types of selector expressions
// Can be either a standard selector or a deep traversal selector
selectorExpression
    : selector
    | deepSelector
    ;

// Standard path selector starting with a slash
// Examples:
// - /Root
// - /Root/Child1
// - /Root/[wildcard]{type=component}
// - /Root/./[wildcard] (current level)
// - /Root/.. (parent level)
selector
    : '/' (nodeName | wildcard | placeholder | currentNode | parentNode) attributeSelector? (nodeSelector)*
    ;

// Deep traversal selector starting with **
// Finds nodes at any depth in the tree
// Examples:
// - [deep]/[wildcard]
// - [deep]/Child2
// - [deep]/[wildcard]{variant=primary}
deepSelector
    : '**' '/' (nodeName | wildcard) attributeSelector? (nodeSelector)*
    ;

// Node selector after the first path segment
// Examples:
// - /Child1 (in /Root/Child1)
// - /[wildcard] (in /Root/[wildcard])
// - /[placeholder] (in /Root/[placeholder])
// - /. (in /Root/.)
// - /.. (in /Root/..)
nodeSelector
    : '/' (nodeName | wildcard | placeholder | currentNode | parentNode) attributeSelector?
    ;

// Named node matching
// Examples: Root, Child1, GrandChild1
nodeName
    : IDENTIFIER
    ;

// Wildcard matching all children
// Example: [wildcard] (in /Root/[wildcard])
wildcard
    : '*'
    ;

// Placeholder matching any nodes at any depth
// Example: [placeholder] (in /Root/[placeholder])
placeholder
    : '~~'
    ;

// Current level operator (stay at same level)
// Example: . (in /Root/./)
currentNode
    : '.'
    ;

// Parent level operator (go up one level)
// Example: .. (in /Root/../)
parentNode
    : '..'
    ;

// Attribute selector for filtering nodes
// Examples:
// - {type=component}
// - {variant=primary}
// - {type=component,version=2.0.0}
// - {'type'='component'}
attributeSelector
    : '{' attributeExpr (',' attributeExpr)* '}'
    ;

// Attribute expression with name and value
// Examples:
// - type=component
// - variant=primary
// - version=2.0.0
// - type=[wildcard]
attributeExpr
    : attributeName '=' attributeValue
    ;

// Attribute name, can be regular identifier or quoted string
// Examples:
// - type
// - 'type'
attributeName
    : IDENTIFIER
    | STRING
    ;

// Attribute value, can be identifier, quoted string, version number, or wildcard
// Examples:
// - component
// - 'component'
// - 2.0.0
// - [wildcard]
attributeValue
    : IDENTIFIER
    | STRING
    | VERSION
    | wildcard  // Allow wildcard as an attribute value
    ;

// Lexer rules

// Identifier for node names and attribute names/values
// Examples: Root, Child1, component, primary
IDENTIFIER
    : [a-zA-Z_][a-zA-Z0-9_]*
    ;

// Version number in semantic versioning format
// Examples: 1.0.0, 2.1, 3, 4.5.2
VERSION
    : [0-9]+ ('.' [0-9]+)* // Matches version numbers like 1.0.0, 2.1, etc.
    ;

// Quoted string for attribute names and values
// Examples: 'type', 'component'
STRING
    : '\'' (~['\\] | '\\' .)* '\''
    ;

// Whitespace is ignored
WS
    : [ \t\r\n]+ -> skip
    ;