// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.arthas.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.github.vudsen.arthasui.language.arthas.psi.ArthasTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ArthasParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, EXTENDS_SETS_);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return root(b, l + 1);
  }

  public static final TokenSet[] EXTENDS_SETS_ = new TokenSet[] {
    create_token_set_(ARGUMENT, AUTH_EXPRESSION, CAT_EXPRESSION, CLASSLOADER_EXPRESSION,
      CLS_STATEMENT, COMMAND, DASHBOARD_STATEMENT, ECHO_EXPRESSION,
      HEAPDUMP_STATEMENT, HELP_EXPRESSION, HISTORY_EXPRESSION, JAD_EXPRESSION,
      JVM_EXPRESSION, KEYMAP_EXPRESSION, LOGGER_EXPRESSION, MC_STATEMENT,
      MEMORY_EXPRESSION, OGNL_STATEMENT, PWD_EXPRESSION, QUIT_STATEMENT,
      RETRANSFORM_STATEMENT, SC_EXPRESSION, SESSION_EXPRESSION, SM_EXPRESSION,
      STACK_STATEMENT, STOP_STATEMENT, SYSENV_EXPRESSION, SYSPROP_EXPRESSION,
      THREAD_EXPRESSION, TRACE_STATEMENT, TT_EXPRESSION, TT_T_STATEMENT,
      UNHANDLED_BASE_64_EXPRESSION, UNHANDLED_DUMP_STATEMENT, UNHANDLED_GETSTATIC_EXPRESSION, UNHANDLED_GREP_EXPRESSION,
      UNHANDLED_JFR_STATEMENT, UNHANDLED_MBEAN_STATEMENT, UNHANDLED_MONITOR_STATEMENT, UNHANDLED_OPTIONS_STATEMENT,
      UNHANDLED_PERFCOUNTER_EXPRESSION, UNHANDLED_PROFILER_STATEMENT, UNHANDLED_REDEFINE_STATEMENT, UNHANDLED_RESET_STATEMENT,
      UNHANDLED_TEE_EXPRESSION, UNHANDLED_VMTOOL_STATEMENT, VERSION_EXPRESSION, VMOPTION_EXPRESSION,
      WATCH_STATEMENT),
  };

  /* ********************************************************** */
  // NON_WHITESPACE_SEQUENCE | IDENTIFIER | ARGS
  static boolean any_sequence(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any_sequence")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<String>");
    r = consumeToken(b, NON_WHITESPACE_SEQUENCE);
    if (!r) r = consumeToken(b, IDENTIFIER);
    if (!r) r = consumeToken(b, ARGS);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ARGS IDENTIFIER?
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    if (!nextTokenIs(b, "<Argument>", ARGS)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT, "<Argument>");
    r = consumeToken(b, ARGS);
    r = r && argument_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IDENTIFIER?
  private static boolean argument_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_1")) return false;
    consumeToken(b, IDENTIFIER);
    return true;
  }

  /* ********************************************************** */
  // 'auth' argument* any_sequence argument*
  public static boolean auth_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "auth_expression")) return false;
    if (!nextTokenIs(b, COMMAND_AUTH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, AUTH_EXPRESSION, null);
    r = consumeToken(b, COMMAND_AUTH);
    p = r; // pin = 1
    r = r && report_error_(b, auth_expression_1(b, l + 1));
    r = p && report_error_(b, any_sequence(b, l + 1)) && r;
    r = p && auth_expression_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // argument*
  private static boolean auth_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "auth_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "auth_expression_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean auth_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "auth_expression_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "auth_expression_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'cat' any_sequence
  public static boolean cat_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cat_expression")) return false;
    if (!nextTokenIs(b, COMMAND_CAT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_CAT);
    r = r && any_sequence(b, l + 1);
    exit_section_(b, m, CAT_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'classloader' argument*
  public static boolean classloader_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classloader_expression")) return false;
    if (!nextTokenIs(b, COMMAND_CLASSLOADER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_CLASSLOADER);
    r = r && classloader_expression_1(b, l + 1);
    exit_section_(b, m, CLASSLOADER_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean classloader_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "classloader_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "classloader_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // CLASS_PATTERN
  public static boolean clazz(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clazz")) return false;
    if (!nextTokenIs(b, "<Class>", CLASS_PATTERN)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CLAZZ, "<Class>");
    r = consumeToken(b, CLASS_PATTERN);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'cls'
  public static boolean cls_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cls_statement")) return false;
    if (!nextTokenIs(b, COMMAND_CLS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_CLS);
    exit_section_(b, m, CLS_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // auth_expression
  //     | unhandled_base64_expression
  //     | cat_expression
  //     | classloader_expression
  //     | cls_statement
  //     | dashboard_statement
  //     | unhandled_dump_statement
  //     | echo_expression
  //     | unhandled_getstatic_expression
  //     | unhandled_grep_expression
  //     | heapdump_statement
  //     | history_expression
  //     | help_expression
  //     | jad_expression
  //     | unhandled_jfr_statement
  //     | jvm_expression
  //     | keymap_expression
  //     | logger_expression
  //     | unhandled_mbean_statement
  //     | mc_statement
  //     | memory_expression
  //     | unhandled_monitor_statement
  //     | ognl_statement
  //     | unhandled_options_statement
  //     | unhandled_perfcounter_expression
  //     | unhandled_profiler_statement
  //     | pwd_expression
  //     | quit_statement
  //     | unhandled_redefine_statement
  //     | unhandled_reset_statement
  //     | retransform_statement
  //     | sc_expression
  //     | session_expression
  //     | sm_expression
  //     | stack_statement
  //     | stop_statement
  //     | sysenv_expression
  //     | sysprop_expression
  //     | unhandled_tee_expression
  //     | thread_expression
  //     | trace_statement
  //     | tt_t_statement
  //     | tt_expression
  //     | version_expression
  //     | vmoption_expression
  //     | unhandled_vmtool_statement
  //     | watch_statement
  public static boolean command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _COLLAPSE_, COMMAND, "<Command>");
    r = auth_expression(b, l + 1);
    if (!r) r = unhandled_base64_expression(b, l + 1);
    if (!r) r = cat_expression(b, l + 1);
    if (!r) r = classloader_expression(b, l + 1);
    if (!r) r = cls_statement(b, l + 1);
    if (!r) r = dashboard_statement(b, l + 1);
    if (!r) r = unhandled_dump_statement(b, l + 1);
    if (!r) r = echo_expression(b, l + 1);
    if (!r) r = unhandled_getstatic_expression(b, l + 1);
    if (!r) r = unhandled_grep_expression(b, l + 1);
    if (!r) r = heapdump_statement(b, l + 1);
    if (!r) r = history_expression(b, l + 1);
    if (!r) r = help_expression(b, l + 1);
    if (!r) r = jad_expression(b, l + 1);
    if (!r) r = unhandled_jfr_statement(b, l + 1);
    if (!r) r = jvm_expression(b, l + 1);
    if (!r) r = keymap_expression(b, l + 1);
    if (!r) r = logger_expression(b, l + 1);
    if (!r) r = unhandled_mbean_statement(b, l + 1);
    if (!r) r = mc_statement(b, l + 1);
    if (!r) r = memory_expression(b, l + 1);
    if (!r) r = unhandled_monitor_statement(b, l + 1);
    if (!r) r = ognl_statement(b, l + 1);
    if (!r) r = unhandled_options_statement(b, l + 1);
    if (!r) r = unhandled_perfcounter_expression(b, l + 1);
    if (!r) r = unhandled_profiler_statement(b, l + 1);
    if (!r) r = pwd_expression(b, l + 1);
    if (!r) r = quit_statement(b, l + 1);
    if (!r) r = unhandled_redefine_statement(b, l + 1);
    if (!r) r = unhandled_reset_statement(b, l + 1);
    if (!r) r = retransform_statement(b, l + 1);
    if (!r) r = sc_expression(b, l + 1);
    if (!r) r = session_expression(b, l + 1);
    if (!r) r = sm_expression(b, l + 1);
    if (!r) r = stack_statement(b, l + 1);
    if (!r) r = stop_statement(b, l + 1);
    if (!r) r = sysenv_expression(b, l + 1);
    if (!r) r = sysprop_expression(b, l + 1);
    if (!r) r = unhandled_tee_expression(b, l + 1);
    if (!r) r = thread_expression(b, l + 1);
    if (!r) r = trace_statement(b, l + 1);
    if (!r) r = tt_t_statement(b, l + 1);
    if (!r) r = tt_expression(b, l + 1);
    if (!r) r = version_expression(b, l + 1);
    if (!r) r = vmoption_expression(b, l + 1);
    if (!r) r = unhandled_vmtool_statement(b, l + 1);
    if (!r) r = watch_statement(b, l + 1);
    exit_section_(b, l, m, r, false, ArthasParser::recover);
    return r;
  }

  /* ********************************************************** */
  // 'dashboard' argument*
  public static boolean dashboard_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dashboard_statement")) return false;
    if (!nextTokenIs(b, COMMAND_DASHBOARD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_DASHBOARD);
    r = r && dashboard_statement_1(b, l + 1);
    exit_section_(b, m, DASHBOARD_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean dashboard_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dashboard_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dashboard_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'echo' string
  public static boolean echo_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "echo_expression")) return false;
    if (!nextTokenIs(b, COMMAND_ECHO)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMAND_ECHO, STRING);
    exit_section_(b, m, ECHO_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'headpdump' argument* any_sequence? argument*
  public static boolean heapdump_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heapdump_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, HEAPDUMP_STATEMENT, "<heapdump statement>");
    r = consumeToken(b, "headpdump");
    r = r && heapdump_statement_1(b, l + 1);
    r = r && heapdump_statement_2(b, l + 1);
    r = r && heapdump_statement_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // argument*
  private static boolean heapdump_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heapdump_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "heapdump_statement_1", c)) break;
    }
    return true;
  }

  // any_sequence?
  private static boolean heapdump_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heapdump_statement_2")) return false;
    any_sequence(b, l + 1);
    return true;
  }

  // argument*
  private static boolean heapdump_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "heapdump_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "heapdump_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'help'
  public static boolean help_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "help_expression")) return false;
    if (!nextTokenIs(b, COMMAND_HELP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_HELP);
    exit_section_(b, m, HELP_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'history' argument?
  public static boolean history_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "history_expression")) return false;
    if (!nextTokenIs(b, COMMAND_HISTORY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_HISTORY);
    r = r && history_expression_1(b, l + 1);
    exit_section_(b, m, HISTORY_EXPRESSION, r);
    return r;
  }

  // argument?
  private static boolean history_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "history_expression_1")) return false;
    argument(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'jad' argument* clazz argument*
  public static boolean jad_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jad_expression")) return false;
    if (!nextTokenIs(b, COMMAND_JAD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_JAD);
    r = r && jad_expression_1(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && jad_expression_3(b, l + 1);
    exit_section_(b, m, JAD_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean jad_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jad_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jad_expression_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean jad_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jad_expression_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "jad_expression_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'jvm'
  public static boolean jvm_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "jvm_expression")) return false;
    if (!nextTokenIs(b, COMMAND_JVM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_JVM);
    exit_section_(b, m, JVM_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'keymap'
  public static boolean keymap_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keymap_expression")) return false;
    if (!nextTokenIs(b, COMMAND_KEYMAP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_KEYMAP);
    exit_section_(b, m, KEYMAP_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'logger'
  public static boolean logger_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logger_expression")) return false;
    if (!nextTokenIs(b, COMMAND_LOGGER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_LOGGER);
    exit_section_(b, m, LOGGER_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'mc' argument* any_sequence argument*
  public static boolean mc_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mc_statement")) return false;
    if (!nextTokenIs(b, COMMAND_MC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_MC);
    r = r && mc_statement_1(b, l + 1);
    r = r && any_sequence(b, l + 1);
    r = r && mc_statement_3(b, l + 1);
    exit_section_(b, m, MC_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean mc_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mc_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mc_statement_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean mc_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mc_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "mc_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'memory'
  public static boolean memory_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "memory_expression")) return false;
    if (!nextTokenIs(b, COMMAND_MEMORY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_MEMORY);
    exit_section_(b, m, MEMORY_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean method(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "method")) return false;
    if (!nextTokenIs(b, "<Method>", IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, METHOD, "<Method>");
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // ognl_item
  public static boolean ognl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ognl")) return false;
    if (!nextTokenIs(b, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = ognl_item(b, l + 1);
    exit_section_(b, m, OGNL, r);
    return r;
  }

  /* ********************************************************** */
  // string
  static boolean ognl_item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ognl_item")) return false;
    if (!nextTokenIs(b, "<Ognl Expression>", STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, null, "<Ognl Expression>");
    r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'ognl' argument* ognl argument*
  public static boolean ognl_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ognl_statement")) return false;
    if (!nextTokenIs(b, COMMAND_OGNL)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, OGNL_STATEMENT, null);
    r = consumeToken(b, COMMAND_OGNL);
    p = r; // pin = 1
    r = r && report_error_(b, ognl_statement_1(b, l + 1));
    r = p && report_error_(b, ognl(b, l + 1)) && r;
    r = p && ognl_statement_3(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // argument*
  private static boolean ognl_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ognl_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ognl_statement_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean ognl_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ognl_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "ognl_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'pwd'
  public static boolean pwd_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pwd_expression")) return false;
    if (!nextTokenIs(b, COMMAND_PWD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_PWD);
    exit_section_(b, m, PWD_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'quit'
  public static boolean quit_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quit_statement")) return false;
    if (!nextTokenIs(b, COMMAND_QUIT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_QUIT);
    exit_section_(b, m, QUIT_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // !(
  //     'auth' | 'base64' | 'cat' | 'classloader' | 'cls' |
  //     'dashboard' | 'dump' | 'echo' | 'getstatic' | 'grep' | 'heapdump' | 'history' | 'jad' | 'jfr' | 'jvm' | 'keymap' |
  //     'logger' | 'mbean' | 'mc' | 'memory' | 'monitor' | 'ognl' | 'options' | 'perfcounter' | 'help' |
  //     'profiler' | 'pwd' | 'quit' | 'redefine' | 'reset' | 'retransform' | 'sc' | 'session' | 'sm' | 'stack' |
  //     'stop' | 'sysenv' | 'sysprop' | 'tee' | 'thread' | 'trace' | 'tt' | 'version' | 'vmoptions' | 'vmtool' | 'watch'
  // )
  static boolean recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // 'auth' | 'base64' | 'cat' | 'classloader' | 'cls' |
  //     'dashboard' | 'dump' | 'echo' | 'getstatic' | 'grep' | 'heapdump' | 'history' | 'jad' | 'jfr' | 'jvm' | 'keymap' |
  //     'logger' | 'mbean' | 'mc' | 'memory' | 'monitor' | 'ognl' | 'options' | 'perfcounter' | 'help' |
  //     'profiler' | 'pwd' | 'quit' | 'redefine' | 'reset' | 'retransform' | 'sc' | 'session' | 'sm' | 'stack' |
  //     'stop' | 'sysenv' | 'sysprop' | 'tee' | 'thread' | 'trace' | 'tt' | 'version' | 'vmoptions' | 'vmtool' | 'watch'
  private static boolean recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "recover_0")) return false;
    boolean r;
    r = consumeToken(b, COMMAND_AUTH);
    if (!r) r = consumeToken(b, COMMAND_BASE64);
    if (!r) r = consumeToken(b, COMMAND_CAT);
    if (!r) r = consumeToken(b, COMMAND_CLASSLOADER);
    if (!r) r = consumeToken(b, COMMAND_CLS);
    if (!r) r = consumeToken(b, COMMAND_DASHBOARD);
    if (!r) r = consumeToken(b, COMMAND_DUMP);
    if (!r) r = consumeToken(b, COMMAND_ECHO);
    if (!r) r = consumeToken(b, COMMAND_GETSTATIC);
    if (!r) r = consumeToken(b, COMMAND_GREP);
    if (!r) r = consumeToken(b, COMMAND_HEAPDUMP);
    if (!r) r = consumeToken(b, COMMAND_HISTORY);
    if (!r) r = consumeToken(b, COMMAND_JAD);
    if (!r) r = consumeToken(b, COMMAND_JFR);
    if (!r) r = consumeToken(b, COMMAND_JVM);
    if (!r) r = consumeToken(b, COMMAND_KEYMAP);
    if (!r) r = consumeToken(b, COMMAND_LOGGER);
    if (!r) r = consumeToken(b, COMMAND_MBEAN);
    if (!r) r = consumeToken(b, COMMAND_MC);
    if (!r) r = consumeToken(b, COMMAND_MEMORY);
    if (!r) r = consumeToken(b, "monitor");
    if (!r) r = consumeToken(b, COMMAND_OGNL);
    if (!r) r = consumeToken(b, COMMAND_OPTIONS);
    if (!r) r = consumeToken(b, COMMAND_PERFCOUNTER);
    if (!r) r = consumeToken(b, COMMAND_HELP);
    if (!r) r = consumeToken(b, COMMAND_PROFILER);
    if (!r) r = consumeToken(b, COMMAND_PWD);
    if (!r) r = consumeToken(b, COMMAND_QUIT);
    if (!r) r = consumeToken(b, COMMAND_REDEFINE);
    if (!r) r = consumeToken(b, COMMAND_RESET);
    if (!r) r = consumeToken(b, COMMAND_RETRANSFORM);
    if (!r) r = consumeToken(b, COMMAND_SC);
    if (!r) r = consumeToken(b, COMMAND_SESSION);
    if (!r) r = consumeToken(b, COMMAND_SM);
    if (!r) r = consumeToken(b, COMMAND_STACK);
    if (!r) r = consumeToken(b, COMMAND_STOP);
    if (!r) r = consumeToken(b, COMMAND_SYSENV);
    if (!r) r = consumeToken(b, COMMAND_SYSPROP);
    if (!r) r = consumeToken(b, COMMAND_TEE);
    if (!r) r = consumeToken(b, COMMAND_THREAD);
    if (!r) r = consumeToken(b, COMMAND_TRACE);
    if (!r) r = consumeToken(b, COMMAND_TT);
    if (!r) r = consumeToken(b, COMMAND_VERSION);
    if (!r) r = consumeToken(b, COMMAND_VMOPTIONS);
    if (!r) r = consumeToken(b, COMMAND_VMTOOL);
    if (!r) r = consumeToken(b, COMMAND_WATCH);
    return r;
  }

  /* ********************************************************** */
  // 'retransform' argument* any_sequence argument*
  public static boolean retransform_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "retransform_statement")) return false;
    if (!nextTokenIs(b, COMMAND_RETRANSFORM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_RETRANSFORM);
    r = r && retransform_statement_1(b, l + 1);
    r = r && any_sequence(b, l + 1);
    r = r && retransform_statement_3(b, l + 1);
    exit_section_(b, m, RETRANSFORM_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean retransform_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "retransform_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "retransform_statement_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean retransform_statement_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "retransform_statement_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "retransform_statement_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // root_item*
  static boolean root(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root")) return false;
    while (true) {
      int c = current_position_(b);
      if (!root_item(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "root", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // !<<eof>> command
  static boolean root_item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item")) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_);
    r = root_item_0(b, l + 1);
    p = r; // pin = 1
    r = r && command(b, l + 1);
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // !<<eof>>
  private static boolean root_item_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item_0")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !eof(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // 'sc' argument* clazz method method? argument*
  public static boolean sc_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sc_expression")) return false;
    if (!nextTokenIs(b, COMMAND_SC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_SC);
    r = r && sc_expression_1(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && method(b, l + 1);
    r = r && sc_expression_4(b, l + 1);
    r = r && sc_expression_5(b, l + 1);
    exit_section_(b, m, SC_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean sc_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sc_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sc_expression_1", c)) break;
    }
    return true;
  }

  // method?
  private static boolean sc_expression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sc_expression_4")) return false;
    method(b, l + 1);
    return true;
  }

  // argument*
  private static boolean sc_expression_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sc_expression_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sc_expression_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'session'
  public static boolean session_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "session_expression")) return false;
    if (!nextTokenIs(b, COMMAND_SESSION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_SESSION);
    exit_section_(b, m, SESSION_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'sm' argument* clazz method? argument*
  public static boolean sm_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sm_expression")) return false;
    if (!nextTokenIs(b, COMMAND_SM)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_SM);
    r = r && sm_expression_1(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && sm_expression_3(b, l + 1);
    r = r && sm_expression_4(b, l + 1);
    exit_section_(b, m, SM_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean sm_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sm_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sm_expression_1", c)) break;
    }
    return true;
  }

  // method?
  private static boolean sm_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sm_expression_3")) return false;
    method(b, l + 1);
    return true;
  }

  // argument*
  private static boolean sm_expression_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sm_expression_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "sm_expression_4", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'stack' argument* clazz method ognl argument*
  public static boolean stack_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stack_statement")) return false;
    if (!nextTokenIs(b, COMMAND_STACK)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_STACK);
    r = r && stack_statement_1(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && method(b, l + 1);
    r = r && ognl(b, l + 1);
    r = r && stack_statement_5(b, l + 1);
    exit_section_(b, m, STACK_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean stack_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stack_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stack_statement_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean stack_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stack_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "stack_statement_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'stop'
  public static boolean stop_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "stop_statement")) return false;
    if (!nextTokenIs(b, COMMAND_STOP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_STOP);
    exit_section_(b, m, STOP_STATEMENT, r);
    return r;
  }

  /* ********************************************************** */
  // 'sysenv' any_sequence?
  public static boolean sysenv_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysenv_expression")) return false;
    if (!nextTokenIs(b, COMMAND_SYSENV)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_SYSENV);
    r = r && sysenv_expression_1(b, l + 1);
    exit_section_(b, m, SYSENV_EXPRESSION, r);
    return r;
  }

  // any_sequence?
  private static boolean sysenv_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysenv_expression_1")) return false;
    any_sequence(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // 'sysprop' (any_sequence | any_sequence any_sequence)?
  public static boolean sysprop_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysprop_expression")) return false;
    if (!nextTokenIs(b, COMMAND_SYSPROP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_SYSPROP);
    r = r && sysprop_expression_1(b, l + 1);
    exit_section_(b, m, SYSPROP_EXPRESSION, r);
    return r;
  }

  // (any_sequence | any_sequence any_sequence)?
  private static boolean sysprop_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysprop_expression_1")) return false;
    sysprop_expression_1_0(b, l + 1);
    return true;
  }

  // any_sequence | any_sequence any_sequence
  private static boolean sysprop_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysprop_expression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any_sequence(b, l + 1);
    if (!r) r = sysprop_expression_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // any_sequence any_sequence
  private static boolean sysprop_expression_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "sysprop_expression_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any_sequence(b, l + 1);
    r = r && any_sequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'thread' argument* any_sequence argument*
  public static boolean thread_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thread_expression")) return false;
    if (!nextTokenIs(b, COMMAND_THREAD)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_THREAD);
    r = r && thread_expression_1(b, l + 1);
    r = r && any_sequence(b, l + 1);
    r = r && thread_expression_3(b, l + 1);
    exit_section_(b, m, THREAD_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean thread_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thread_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "thread_expression_1", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean thread_expression_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "thread_expression_3")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "thread_expression_3", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'trace' argument* clazz method ognl? argument*
  public static boolean trace_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trace_statement")) return false;
    if (!nextTokenIs(b, COMMAND_TRACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_TRACE);
    r = r && trace_statement_1(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && method(b, l + 1);
    r = r && trace_statement_4(b, l + 1);
    r = r && trace_statement_5(b, l + 1);
    exit_section_(b, m, TRACE_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean trace_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trace_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "trace_statement_1", c)) break;
    }
    return true;
  }

  // ognl?
  private static boolean trace_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trace_statement_4")) return false;
    ognl(b, l + 1);
    return true;
  }

  // argument*
  private static boolean trace_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "trace_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "trace_statement_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'tt' argument*
  public static boolean tt_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tt_expression")) return false;
    if (!nextTokenIs(b, COMMAND_TT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_TT);
    r = r && tt_expression_1(b, l + 1);
    exit_section_(b, m, TT_EXPRESSION, r);
    return r;
  }

  // argument*
  private static boolean tt_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tt_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tt_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'tt' '-t' argument* clazz method argument*
  public static boolean tt_t_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tt_t_statement")) return false;
    if (!nextTokenIs(b, COMMAND_TT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_TT);
    r = r && consumeToken(b, "-t");
    r = r && tt_t_statement_2(b, l + 1);
    r = r && clazz(b, l + 1);
    r = r && method(b, l + 1);
    r = r && tt_t_statement_5(b, l + 1);
    exit_section_(b, m, TT_T_STATEMENT, r);
    return r;
  }

  // argument*
  private static boolean tt_t_statement_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tt_t_statement_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tt_t_statement_2", c)) break;
    }
    return true;
  }

  // argument*
  private static boolean tt_t_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tt_t_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tt_t_statement_5", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'base64' any_sequence*
  public static boolean unhandled_base64_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_base64_expression")) return false;
    if (!nextTokenIs(b, COMMAND_BASE64)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_BASE64);
    r = r && unhandled_base64_expression_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_BASE_64_EXPRESSION, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_base64_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_base64_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_base64_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'dump' any_sequence*
  public static boolean unhandled_dump_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_dump_statement")) return false;
    if (!nextTokenIs(b, COMMAND_DUMP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_DUMP);
    r = r && unhandled_dump_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_DUMP_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_dump_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_dump_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_dump_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'getstatic' any_sequence*
  public static boolean unhandled_getstatic_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_getstatic_expression")) return false;
    if (!nextTokenIs(b, COMMAND_GETSTATIC)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_GETSTATIC);
    r = r && unhandled_getstatic_expression_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_GETSTATIC_EXPRESSION, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_getstatic_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_getstatic_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_getstatic_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'grep' any_sequence*
  public static boolean unhandled_grep_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_grep_expression")) return false;
    if (!nextTokenIs(b, COMMAND_GREP)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_GREP);
    r = r && unhandled_grep_expression_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_GREP_EXPRESSION, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_grep_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_grep_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_grep_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'jfr' any_sequence*
  public static boolean unhandled_jfr_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_jfr_statement")) return false;
    if (!nextTokenIs(b, COMMAND_JFR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_JFR);
    r = r && unhandled_jfr_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_JFR_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_jfr_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_jfr_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_jfr_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'mbean' any_sequence*
  public static boolean unhandled_mbean_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_mbean_statement")) return false;
    if (!nextTokenIs(b, COMMAND_MBEAN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_MBEAN);
    r = r && unhandled_mbean_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_MBEAN_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_mbean_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_mbean_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_mbean_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'monitor' any_sequence*
  public static boolean unhandled_monitor_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_monitor_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNHANDLED_MONITOR_STATEMENT, "<unhandled monitor statement>");
    r = consumeToken(b, "monitor");
    r = r && unhandled_monitor_statement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_monitor_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_monitor_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_monitor_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'options' any_sequence*
  public static boolean unhandled_options_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_options_statement")) return false;
    if (!nextTokenIs(b, COMMAND_OPTIONS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_OPTIONS);
    r = r && unhandled_options_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_OPTIONS_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_options_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_options_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_options_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'perfcounter' any_sequence*
  public static boolean unhandled_perfcounter_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_perfcounter_expression")) return false;
    if (!nextTokenIs(b, COMMAND_PERFCOUNTER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_PERFCOUNTER);
    r = r && unhandled_perfcounter_expression_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_PERFCOUNTER_EXPRESSION, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_perfcounter_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_perfcounter_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_perfcounter_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'unhandled' any_sequence*
  public static boolean unhandled_profiler_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_profiler_statement")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, UNHANDLED_PROFILER_STATEMENT, "<unhandled profiler statement>");
    r = consumeToken(b, "unhandled");
    r = r && unhandled_profiler_statement_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_profiler_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_profiler_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_profiler_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'redefine' any_sequence*
  public static boolean unhandled_redefine_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_redefine_statement")) return false;
    if (!nextTokenIs(b, COMMAND_REDEFINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_REDEFINE);
    r = r && unhandled_redefine_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_REDEFINE_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_redefine_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_redefine_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_redefine_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'reset' any_sequence*
  public static boolean unhandled_reset_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_reset_statement")) return false;
    if (!nextTokenIs(b, COMMAND_RESET)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_RESET);
    r = r && unhandled_reset_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_RESET_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_reset_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_reset_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_reset_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'tee' any_sequence*
  public static boolean unhandled_tee_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_tee_expression")) return false;
    if (!nextTokenIs(b, COMMAND_TEE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_TEE);
    r = r && unhandled_tee_expression_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_TEE_EXPRESSION, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_tee_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_tee_expression_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_tee_expression_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'vmtool' any_sequence*
  public static boolean unhandled_vmtool_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_vmtool_statement")) return false;
    if (!nextTokenIs(b, COMMAND_VMTOOL)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_VMTOOL);
    r = r && unhandled_vmtool_statement_1(b, l + 1);
    exit_section_(b, m, UNHANDLED_VMTOOL_STATEMENT, r);
    return r;
  }

  // any_sequence*
  private static boolean unhandled_vmtool_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unhandled_vmtool_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any_sequence(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unhandled_vmtool_statement_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // 'version'
  public static boolean version_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "version_expression")) return false;
    if (!nextTokenIs(b, COMMAND_VERSION)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_VERSION);
    exit_section_(b, m, VERSION_EXPRESSION, r);
    return r;
  }

  /* ********************************************************** */
  // 'vmoptions' (any_sequence | any_sequence any_sequence)?
  public static boolean vmoption_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "vmoption_expression")) return false;
    if (!nextTokenIs(b, COMMAND_VMOPTIONS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMAND_VMOPTIONS);
    r = r && vmoption_expression_1(b, l + 1);
    exit_section_(b, m, VMOPTION_EXPRESSION, r);
    return r;
  }

  // (any_sequence | any_sequence any_sequence)?
  private static boolean vmoption_expression_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "vmoption_expression_1")) return false;
    vmoption_expression_1_0(b, l + 1);
    return true;
  }

  // any_sequence | any_sequence any_sequence
  private static boolean vmoption_expression_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "vmoption_expression_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any_sequence(b, l + 1);
    if (!r) r = vmoption_expression_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // any_sequence any_sequence
  private static boolean vmoption_expression_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "vmoption_expression_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any_sequence(b, l + 1);
    r = r && any_sequence(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // 'watch' argument* clazz method ognl? argument*
  public static boolean watch_statement(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "watch_statement")) return false;
    if (!nextTokenIs(b, COMMAND_WATCH)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, WATCH_STATEMENT, null);
    r = consumeToken(b, COMMAND_WATCH);
    p = r; // pin = 1
    r = r && report_error_(b, watch_statement_1(b, l + 1));
    r = p && report_error_(b, clazz(b, l + 1)) && r;
    r = p && report_error_(b, method(b, l + 1)) && r;
    r = p && report_error_(b, watch_statement_4(b, l + 1)) && r;
    r = p && watch_statement_5(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // argument*
  private static boolean watch_statement_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "watch_statement_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "watch_statement_1", c)) break;
    }
    return true;
  }

  // ognl?
  private static boolean watch_statement_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "watch_statement_4")) return false;
    ognl(b, l + 1);
    return true;
  }

  // argument*
  private static boolean watch_statement_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "watch_statement_5")) return false;
    while (true) {
      int c = current_position_(b);
      if (!argument(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "watch_statement_5", c)) break;
    }
    return true;
  }

}
