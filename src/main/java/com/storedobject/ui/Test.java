package com.storedobject.ui;

import com.storedobject.chart.Color;
import com.storedobject.chart.GanttChart;
import com.storedobject.chart.Project;
import com.storedobject.chart.SOChart;
import com.storedobject.vaadin.CloseableView;
import com.storedobject.vaadin.View;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.LocalDateTime;

public class Test extends View implements CloseableView {

    public Test() {
        super("Gantt Chart Example");

        // Define a chart component
        SOChart soChart = new SOChart();
        soChart.setSize("900px", "500px");

        // Sample project with few entries
        Project project = new Project();
        project.setStart(LocalDateTime.now().minusDays(10));
        Project.TaskGroup tg1 = project.createTaskGroup("Group 1");
        Project.Task tg1T1 = tg1.createTask("Task 1/1", 6);
        Project.Task tg1T2 = tg1.createTask("Task 1/2", 5);
        tg1T2.setCompleted(100); // This task is 100% complete
        project.dependsOn(tg1T2, tg1T1);
        Project.Task tg1T3 = tg1.createTask("Task 1/3", 11);
        Project.Task milestone = tg1.createTask("Example Milestone", 0); // Milestone
        project.dependsOn(milestone, tg1T3);
        project.dependsOn(tg1T2, milestone);
        Project.TaskGroup tg2 = project.createTaskGroup("Group 2");
        Project.Task tg2T1 = tg2.createTask("Task 2/1", 3);
        Project.Task tg2T2 = tg2.createTask("Task 2/2", 7);
        Project.Task tg2T3 = tg2.createTask("Task 2/3", 13);
        tg2.createTask("Task 2/4", 9);
        tg2T3.setColor(new Color("green")); // Specific color for this task
        tg2T1.setCompleted(35); // This task is 35% complete
        project.dependsOn(tg2T2, tg1);
        project.dependsOn(tg1T2, tg2T1);

        // Plot the project on a Gantt Chart
        GanttChart gc = new GanttChart(project);

        // Add the Gantt Chart to our chart component
        soChart.add(gc);

        setComponent(new VerticalLayout(soChart));
    }
}
