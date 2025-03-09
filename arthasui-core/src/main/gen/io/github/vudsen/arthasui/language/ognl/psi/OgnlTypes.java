// This is a generated file. Not intended for manual editing.
package io.github.vudsen.arthasui.language.ognl.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import io.github.vudsen.arthasui.language.ognl.psi.impl.*;

public interface OgnlTypes {

  IElementType CLASS_NAME = new OgnlElementType("CLASS_NAME");
  IElementType CLAZZ = new OgnlElementType("CLAZZ");
  IElementType CLAZZ_PACKAGE = new OgnlElementType("CLAZZ_PACKAGE");
  IElementType DYNAMIC_EXPRESSION_LIST = new OgnlElementType("DYNAMIC_EXPRESSION_LIST");
  IElementType EXPRESSION = new OgnlElementType("EXPRESSION");
  IElementType EXPRESSION_LIST = new OgnlElementType("EXPRESSION_LIST");
  IElementType IDENTIFIER_EXPRESSION = new OgnlElementType("IDENTIFIER_EXPRESSION");
  IElementType LITERAL_EXPRESSION = new OgnlElementType("LITERAL_EXPRESSION");
  IElementType METHOD_CALL_EXPRESSION = new OgnlElementType("METHOD_CALL_EXPRESSION");
  IElementType NEW_EXPRESSION = new OgnlElementType("NEW_EXPRESSION");
  IElementType REFERENCE_EXPRESSION = new OgnlElementType("REFERENCE_EXPRESSION");
  IElementType STATIC_REF_EXPRESSION = new OgnlElementType("STATIC_REF_EXPRESSION");

  IElementType AT = new OgnlTokenType("@");
  IElementType DOT = new OgnlTokenType(".");
  IElementType EOL = new OgnlTokenType("EOL");
  IElementType EQ = new OgnlTokenType("=");
  IElementType IDENTIFIER = new OgnlTokenType("IDENTIFIER");
  IElementType KEY_WORD_COMMA = new OgnlTokenType(",");
  IElementType KEY_WORD_NEW = new OgnlTokenType("new");
  IElementType LEFT_SQUARE_BRACKET = new OgnlTokenType("[");
  IElementType LP = new OgnlTokenType("(");
  IElementType NON_ESCAPE_CHARACTER = new OgnlTokenType("NON_ESCAPE_CHARACTER");
  IElementType NUMBER = new OgnlTokenType("number");
  IElementType OPERATION = new OgnlTokenType("OPERATION");
  IElementType RIGHT_SQUARE_BRACKET = new OgnlTokenType("]");
  IElementType RP = new OgnlTokenType(")");
  IElementType SEMI = new OgnlTokenType(";");
  IElementType STRING = new OgnlTokenType("string");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == CLASS_NAME) {
        return new OgnlClassNameImpl(node);
      }
      else if (type == CLAZZ) {
        return new OgnlClazzImpl(node);
      }
      else if (type == CLAZZ_PACKAGE) {
        return new OgnlClazzPackageImpl(node);
      }
      else if (type == DYNAMIC_EXPRESSION_LIST) {
        return new OgnlDynamicExpressionListImpl(node);
      }
      else if (type == EXPRESSION) {
        return new OgnlExpressionImpl(node);
      }
      else if (type == EXPRESSION_LIST) {
        return new OgnlExpressionListImpl(node);
      }
      else if (type == IDENTIFIER_EXPRESSION) {
        return new OgnlIdentifierExpressionImpl(node);
      }
      else if (type == LITERAL_EXPRESSION) {
        return new OgnlLiteralExpressionImpl(node);
      }
      else if (type == METHOD_CALL_EXPRESSION) {
        return new OgnlMethodCallExpressionImpl(node);
      }
      else if (type == NEW_EXPRESSION) {
        return new OgnlNewExpressionImpl(node);
      }
      else if (type == REFERENCE_EXPRESSION) {
        return new OgnlReferenceExpressionImpl(node);
      }
      else if (type == STATIC_REF_EXPRESSION) {
        return new OgnlStaticRefExpressionImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
