package com.example.taskreminder;

import java.time.LocalDate;
import java.time.LocalTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Tasks {
    private StringProperty task;
    private ObjectProperty<LocalTime> time;
    private ObjectProperty<LocalDate> date;

    public Tasks(String task, LocalTime time, LocalDate date) {
        this.task = new SimpleStringProperty(task);
        this.time = new SimpleObjectProperty<>(time);
        this.date = new SimpleObjectProperty<>(date);
    }

    public String getTask() {
        return task.get();
    }

    public void setTask(String task) {
        this.task.set(task);
    }

    public StringProperty taskProperty() {
        return task;
    }

    public LocalTime getTime() {
        return time.get();
    }

    public void setTime(LocalTime time) {
        this.time.set(time);
    }

    public ObjectProperty<LocalTime> timeProperty() {
        return time;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate date) {
        this.date.set(date);
    }

    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }
}
