package chocopy.common.codegen;

import java.util.HashMap;
import java.util.Map;

import chocopy.common.astnodes.BooleanLiteral;
import chocopy.common.astnodes.IntegerLiteral;
import chocopy.common.astnodes.Literal;
import chocopy.common.astnodes.NoneLiteral;
import chocopy.common.astnodes.StringLiteral;

/**
 * A store for caching and re-using program constants.
 *
 * Constants are emitted in assembly in the DATA section,
 * and therefore are represented by their labels.
 *
 * @see {@link Label}
 */
public class Constants {

    /** A counter used to generate unique label names for constants. */
    protected int nextLabelSuffix = 0;

    /** The constant representing the boolean `False` */
    final Label falseConstant = generateConstantLabel();

    /** The constant representing the boolean `True` */
    final Label trueConstant = generateConstantLabel();

    /** A cache for integer-valued constants. */
    final Map<Integer, Label> intConstants = new HashMap<>();

    /** A cache for string-valued constants. */
    final Map<String, Label> strConstants = new HashMap<>();

    /**
     * Returns the next unique label suffix for constants.
     *
     * @return the next unique label suffix for constants
     */
    protected int getNextLabelSuffix() {
        return nextLabelSuffix++;
    }

    /**
     * Generates a fresh label for constants.
     *
     * This label is guaranteed to be unique amongst labels
     * generated by invoking this method. All such labels
     * have a prefix of `const_`.
     *
     * @return a fresh label
     */
    public Label generateConstantLabel() {
        return new Label(String.format("const_%d", getNextLabelSuffix()));
    }

    /**
     * Returns the label for a `bool` constant.
     *
     * @param value the boolean value
     * @return the label for the boolean value
     */
    public Label getBoolConstant(boolean value) {
        return value ? trueConstant : falseConstant;
    }

    /**
     * Returns the label for am `int` constant.
     *
     * @param value the integer value
     * @return the label for the integer value
     */
    public Label getIntConstant(int value) {
        if (intConstants.containsKey(value)) {
            return intConstants.get(value);
        } else {
            Label newLabel = generateConstantLabel();
            intConstants.put(value, newLabel);
            return newLabel;
        }
    }

    /**
     * Returns the label for a `str` constant.
     *
     * @param value the string value
     * @return the label for the string value
     */
    public Label getStrConstant(String value) {
        if (strConstants.containsKey(value)) {
            return strConstants.get(value);
        } else {
            Label newLabel = generateConstantLabel();
            strConstants.put(value, newLabel);
            return newLabel;
        }
    }

    /**
     * Converts a constant literal in the AST to a constant
     * for code generation.
     *
     * @param literal the literal expression in the AST
     * @return a {@link Label} representing a constant int/str/bool,
     *         or `null` representing the None literal
     */
    public Label fromLiteral(Literal literal) {
        if (literal instanceof IntegerLiteral) {
            return getIntConstant(((IntegerLiteral) literal).value);
        } else if (literal instanceof StringLiteral) {
            return getStrConstant(((StringLiteral) literal).value);
        } else if (literal instanceof BooleanLiteral) {
            return getBoolConstant(((BooleanLiteral) literal).value);
        } else {
            assert literal instanceof NoneLiteral;
            return null;
        }
    }
}
