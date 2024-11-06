package jingdutiao.model;

import java.util.List;

public class Project {
    private String name;
    private List<Double> steps; // 存储每个步骤的工时

    public Project(String name, List<Double> steps) {
        this.name = name;
        this.steps = steps;
    }

    // 其他必要的方法，比如计算进度等
}
