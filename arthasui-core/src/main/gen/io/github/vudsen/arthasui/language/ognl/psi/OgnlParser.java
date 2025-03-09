// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.ognl.psi;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static io.github.vudsen.arthasui.language.ognl.psi.OgnlTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class OgnlParser implements PsiParser, LightPsiParser {

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
    create_token_set_(EXPRESSION, IDENTIFIER_EXPRESSION, LITERAL_EXPRESSION, METHOD_CALL_EXPRESSION,
      NEW_EXPRESSION, REFERENCE_EXPRESSION, STATIC_REF_EXPRESSION),
  };

  /* ********************************************************** */
  // IDENTIFIER
  public static boolean class_name(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "class_name")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IDENTIFIER);
    exit_section_(b, m, CLASS_NAME, r);
    return r;
  }

  /* ********************************************************** */
  // clazz_package* class_name
  public static boolean clazz(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clazz")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = clazz_0(b, l + 1);
    r = r && class_name(b, l + 1);
    exit_section_(b, m, CLAZZ, r);
    return r;
  }

  // clazz_package*
  private static boolean clazz_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clazz_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!clazz_package(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "clazz_0", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // IDENTIFIER '.'
  public static boolean clazz_package(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "clazz_package")) return false;
    if (!nextTokenIs(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, IDENTIFIER, DOT);
    exit_section_(b, m, CLAZZ_PACKAGE, r);
    return r;
  }

  /* ********************************************************** */
  // '[' (expression (',' expression)*) ']'
  public static boolean dynamic_expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression_list")) return false;
    if (!nextTokenIs(b, LEFT_SQUARE_BRACKET)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, DYNAMIC_EXPRESSION_LIST, null);
    r = consumeToken(b, LEFT_SQUARE_BRACKET);
    p = r; // pin = 1
    r = r && report_error_(b, dynamic_expression_list_1(b, l + 1));
    r = p && consumeToken(b, RIGHT_SQUARE_BRACKET) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // expression (',' expression)*
  private static boolean dynamic_expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression_list_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1, -1);
    r = r && dynamic_expression_list_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' expression)*
  private static boolean dynamic_expression_list_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression_list_1_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!dynamic_expression_list_1_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "dynamic_expression_list_1_1", c)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean dynamic_expression_list_1_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "dynamic_expression_list_1_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEY_WORD_COMMA);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // '(' (expression (',' expression)*)? ')'
  public static boolean expression_list(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list")) return false;
    if (!nextTokenIs(b, LP)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, EXPRESSION_LIST, null);
    r = consumeToken(b, LP);
    p = r; // pin = 1
    r = r && report_error_(b, expression_list_1(b, l + 1));
    r = p && consumeToken(b, RP) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // (expression (',' expression)*)?
  private static boolean expression_list_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1")) return false;
    expression_list_1_0(b, l + 1);
    return true;
  }

  // expression (',' expression)*
  private static boolean expression_list_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1, -1);
    r = r && expression_list_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (',' expression)*
  private static boolean expression_list_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!expression_list_1_0_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "expression_list_1_0_1", c)) break;
    }
    return true;
  }

  // ',' expression
  private static boolean expression_list_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_list_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, KEY_WORD_COMMA);
    r = r && expression(b, l + 1, -1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // !(EOL | 'new' | '[' | '@' clazz '@' | string | number)
  static boolean expression_recover(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_recover")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NOT_);
    r = !expression_recover_0(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // EOL | 'new' | '[' | '@' clazz '@' | string | number
  private static boolean expression_recover_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_recover_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOL);
    if (!r) r = consumeToken(b, KEY_WORD_NEW);
    if (!r) r = consumeToken(b, LEFT_SQUARE_BRACKET);
    if (!r) r = expression_recover_0_3(b, l + 1);
    if (!r) r = consumeToken(b, STRING);
    if (!r) r = consumeToken(b, NUMBER);
    exit_section_(b, m, null, r);
    return r;
  }

  // '@' clazz '@'
  private static boolean expression_recover_0_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "expression_recover_0_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, AT);
    r = r && clazz(b, l + 1);
    r = r && consumeToken(b, AT);
    exit_section_(b, m, null, r);
    return r;
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
  // EOL | expression EOL?
  static boolean root_item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EOL);
    if (!r) r = root_item_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // expression EOL?
  private static boolean root_item_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = expression(b, l + 1, -1);
    r = r && root_item_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EOL?
  private static boolean root_item_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "root_item_1_1")) return false;
    consumeToken(b, EOL);
    return true;
  }

  /* ********************************************************** */
  // Expression root: expression
  // Operator priority table:
  // 0: POSTFIX(method_call_expression)
  // 1: POSTFIX(reference_expression)
  // 2: ATOM(new_expression)
  // 3: ATOM(static_ref_expression)
  // 4: ATOM(literal_expression)
  // 5: ATOM(identifier_expression)
  public static boolean expression(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression")) return false;
    addVariant(b, "<expression>");
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, "<expression>");
    r = new_expression(b, l + 1);
    if (!r) r = static_ref_expression(b, l + 1);
    if (!r) r = literal_expression(b, l + 1);
    if (!r) r = identifier_expression(b, l + 1);
    p = r;
    r = r && expression_0(b, l + 1, g);
    exit_section_(b, l, m, null, r, p, null);
    return r || p;
  }

  public static boolean expression_0(PsiBuilder b, int l, int g) {
    if (!recursion_guard_(b, l, "expression_0")) return false;
    boolean r = true;
    while (true) {
      Marker m = enter_section_(b, l, _LEFT_, null);
      if (g < 0 && expression_list(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, METHOD_CALL_EXPRESSION, r, true, null);
      }
      else if (g < 1 && reference_expression_0(b, l + 1)) {
        r = true;
        exit_section_(b, l, m, REFERENCE_EXPRESSION, r, true, null);
      }
      else {
        exit_section_(b, l, m, null, false, false, null);
        break;
      }
    }
    return r;
  }

  // '.' identifier_expression | dynamic_expression_list
  private static boolean reference_expression_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reference_expression_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = reference_expression_0_0(b, l + 1);
    if (!r) r = dynamic_expression_list(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // '.' identifier_expression
  private static boolean reference_expression_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "reference_expression_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, DOT);
    r = r && identifier_expression(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // 'new' clazz expression_list
  public static boolean new_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "new_expression")) return false;
    if (!nextTokenIsSmart(b, KEY_WORD_NEW)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, NEW_EXPRESSION, null);
    r = consumeTokenSmart(b, KEY_WORD_NEW);
    p = r; // pin = 1
    r = r && report_error_(b, clazz(b, l + 1));
    r = p && expression_list(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // '@' clazz '@' identifier_expression
  public static boolean static_ref_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "static_ref_expression")) return false;
    if (!nextTokenIsSmart(b, AT)) return false;
    boolean r, p;
    Marker m = enter_section_(b, l, _NONE_, STATIC_REF_EXPRESSION, null);
    r = consumeTokenSmart(b, AT);
    p = r; // pin = 1
    r = r && report_error_(b, clazz(b, l + 1));
    r = p && report_error_(b, consumeToken(b, AT)) && r;
    r = p && identifier_expression(b, l + 1) && r;
    exit_section_(b, l, m, r, p, null);
    return r || p;
  }

  // string | number
  public static boolean literal_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "literal_expression")) return false;
    if (!nextTokenIsSmart(b, NUMBER, STRING)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LITERAL_EXPRESSION, "<literal expression>");
    r = consumeTokenSmart(b, STRING);
    if (!r) r = consumeTokenSmart(b, NUMBER);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IDENTIFIER
  public static boolean identifier_expression(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "identifier_expression")) return false;
    if (!nextTokenIsSmart(b, IDENTIFIER)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokenSmart(b, IDENTIFIER);
    exit_section_(b, m, IDENTIFIER_EXPRESSION, r);
    return r;
  }

}
