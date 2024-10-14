package model;


import static util.InputUtil.toNormalize;

import lombok.Data;

@Data
public class TreeNode {

    private String expressionData;
    private TreeNode operandLeft;
    private TreeNode operandRight;
    private boolean wrappedByParenthesis;
    private String trimmedValue = "";
    private String leftParenthesis = "";
    private String rightParenthesis = "";
    public TreeNode(String expressionData) {
        this.expressionData = expressionData;
        this.trimmedValue = toNormalize(expressionData);
    }

}