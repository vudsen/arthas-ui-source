package io.github.vudsen.arthasui.language.ognl;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static io.github.vudsen.arthasui.language.ognl.psi.OgnlTypes.*;

%%

%{
  public _OgnlLexer() {
    this((java.io.Reader)null);
  }
%}

%public
%class _OgnlLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

NUMBER=[0-9]+(\.[0-9]*)?
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
IDENTIFIER=[a-zA-Z\d]+
OPERATION=[+\-*/&~!]
EOL=\n
NON_ESCAPE_CHARACTER=[^\"\\]

%%
<YYINITIAL> {
  {WHITE_SPACE}                { return WHITE_SPACE; }

  ";"                          { return SEMI; }
  "="                          { return EQ; }
  "("                          { return LP; }
  ")"                          { return RP; }
  "."                          { return DOT; }
  "@"                          { return AT; }
  "["                          { return LEFT_SQUARE_BRACKET; }
  "]"                          { return RIGHT_SQUARE_BRACKET; }
  ","                          { return KEY_WORD_COMMA; }
  "new"                        { return KEY_WORD_NEW; }

  {NUMBER}                     { return NUMBER; }
  {STRING}                     { return STRING; }
  {IDENTIFIER}                 { return IDENTIFIER; }
  {OPERATION}                  { return OPERATION; }
  {EOL}                        { return EOL; }
  {NON_ESCAPE_CHARACTER}       { return NON_ESCAPE_CHARACTER; }

}

[^] { return BAD_CHARACTER; }
