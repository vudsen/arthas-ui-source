package io.github.vudsen.arthasui.language.arthas;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;import com.intellij.ui.mac.foundation.ID;

import java.util.Stack;import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;
import static io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes.*;

%%

%{

    private Stack<Integer> states = new Stack<>();


    private void beginState(int state) {
        states.push(state);
        yybegin(state);
    }

    private void popState() {
        states.pop();
        if (states.isEmpty()) {
            yybegin(YYINITIAL);
        } else {
            yybegin(states.peek());
        }
    }

    public _ArthasLexer() {
      this((java.io.Reader)null);
    }

    private static String zzToPrintable(CharSequence str) {
        return zzToPrintable(str.toString());
    }
%}
%debug

%public
%class _ArthasLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode

EOL=\R
WHITE_SPACE=\s+

SPACE=[ \t\n\x0B\f\r]+
EOL=\n
STRING=('([^'\\]|\\.)*'|\"([^\"\\]|\\.)*\")
LINE_COMMENT="//".*
ARGUMENT_HEAD=-[a-zA-Z]+
IDENTIFIER=[a-zA-Z\d]+
NON_WHITESPACE_SEQUENCE=[^\n ]+
CLASS_PATTERN=[a-zA-Z\d]+(\.[a-zA-Z\d]+)*
%states CLAZZ_METHOD_PAIR_1, CLAZZ_METHOD_PAIR_2
%states WAITING_ARGUMENT_VALYE
%%

<WAITING_ARGUMENT_VALYE> {
    {IDENTIFIER}  { popState(); return IDENTIFIER; }
}

<CLAZZ_METHOD_PAIR_1> {
    {CLASS_PATTERN}               { beginState(CLAZZ_METHOD_PAIR_2); return CLASS_PATTERN; }
    {ARGUMENT_HEAD}                    { beginState(WAITING_ARGUMENT_VALYE); return ARGS; }
    <CLAZZ_METHOD_PAIR_2> {
        {IDENTIFIER}                  { popState(); popState(); return IDENTIFIER; }
    }
}


<YYINITIAL> {
  {WHITE_SPACE}                   { return WHITE_SPACE; }

  ";"                             { return SEMICOLON; }
  "."                             { return DOT; }
  "auth"                          { return COMMAND_AUTH; }
  "base64"                        { return COMMAND_BASE64; }
  "cat"                           { return COMMAND_CAT; }
  "classloader"                   { return COMMAND_CLASSLOADER; }
  "cls"                           { return COMMAND_CLS; }
  "dashboard"                     { return COMMAND_DASHBOARD; }
  "dump"                          { return COMMAND_DUMP; }
  "echo"                          { return COMMAND_ECHO; }
  "getstatic"                     { return COMMAND_GETSTATIC; }
  "grep"                          { return COMMAND_GREP; }
  "heapdump"                      { return COMMAND_HEAPDUMP; }
  "help"                          { return COMMAND_HELP; }
  "history"                       { return COMMAND_HISTORY; }
  "jad"                           { return COMMAND_JAD; }
  "jfr"                           { return COMMAND_JFR; }
  "jvm"                           { return COMMAND_JVM; }
  "keymap"                        { return COMMAND_KEYMAP; }
  "logger"                        { return COMMAND_LOGGER; }
  "mbean"                         { return COMMAND_MBEAN; }
  "mc"                            { return COMMAND_MC; }
  "memory"                        { return COMMAND_MEMORY; }
  "options"                       { return COMMAND_OPTIONS; }
  "watch"                         { beginState(CLAZZ_METHOD_PAIR_1);return COMMAND_WATCH; }
  "ognl"                          { return COMMAND_OGNL; }
  "perfcounter"                   { return COMMAND_PERFCOUNTER; }
  "profiler"                      { return COMMAND_PROFILER; }
  "pwd"                           { return COMMAND_PWD; }
  "quit"                          { return COMMAND_QUIT; }
  "redefine"                      { return COMMAND_REDEFINE; }
  "reset"                         { return COMMAND_RESET; }
  "retransform"                   { return COMMAND_RETRANSFORM; }
  "sc"                            { return COMMAND_SC; }
  "session"                       { return COMMAND_SESSION; }
  "sm"                            { return COMMAND_SM; }
  "stack"                         { return COMMAND_STACK; }
  "stop"                          { return COMMAND_STOP; }
  "sysenv"                        { return COMMAND_SYSENV; }
  "sysprop"                       { return COMMAND_SYSPROP; }
  "tee"                           { return COMMAND_TEE; }
  "thread"                        { return COMMAND_THREAD; }
  "trace"                         { return COMMAND_TRACE; }
  "tt"                            { return COMMAND_TT; }
  "version"                       { return COMMAND_VERSION; }
  "vmoptions"                     { return COMMAND_VMOPTIONS; }
  "vmtool"                        { return COMMAND_VMTOOL; }

  {SPACE}                         { return SPACE; }
  {EOL}                           { return EOL; }
  {STRING}                        { return STRING; }
  {LINE_COMMENT}                  { return LINE_COMMENT; }
  {ARGUMENT_HEAD}                 { beginState(WAITING_ARGUMENT_VALYE); return ARGUMENT_HEAD; }
  {IDENTIFIER}                    { return IDENTIFIER; }
  {NON_WHITESPACE_SEQUENCE}       { return NON_WHITESPACE_SEQUENCE; }



}
" " { return WHITE_SPACE; }
[^] { return BAD_CHARACTER; }
