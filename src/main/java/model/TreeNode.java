package model;


import lombok.Data;

@Data
public class TreeNode {

    private String expressionData;
    private TreeNode operandLeft;
    private TreeNode operandRight;
    public TreeNode(String expressionData) {
        this.expressionData = expressionData;
    }
}