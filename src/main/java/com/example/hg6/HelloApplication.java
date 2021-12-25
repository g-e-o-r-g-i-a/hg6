package com.example.hg6;

import Controller.EnrolledContr;
import Controller.CourseContr;
import Controller.TeacherContr;
import Controller.StudentContr;
import Model.Enrolled;
import Model.Course;
import Model.Teacher;
import Model.Student;
import Repository.EnrolledRepo;
import Repository.CourseRepo;
import Repository.TeacherRepo;
import Repository.StudentRepo;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import Exception.EmptyArray;
import Exception.AlreadyExist;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloApplication extends Application {

    private StudentContr studentContr;
    private TeacherContr teacherContr;
    private EnrolledContr enrolledContr;
    private CourseContr courseContr;
    String settings = """
            -fx-background-color:\s
                   #FA8072,
                   linear-gradient(#5DADE2, #76D7C4),
                   linear-gradient(#F9E79F 4%, #E8DAEF 40%, #F2D7D5 62%, #F7DC6F 100%);
               -fx-background-insets: 0,1,2;
               -fx-background-radius: 4,3,2;
               -fx-padding: 1 50 1 50;
               -fx-text-fill: black;
               -fx-font-size: 18px;""".indent(1);
    String background = " -fx-background-color: #A9CCE3";
    String textUserStyle = """
            -fx-background-color:\s
                   #DC7633,
                   linear-gradient(#E8DAEF, #FCF3CF),
                   linear-gradient(#F9E79F 4%, #E8DAEF 40%, #F2D7D5 62%, #F7DC6F 100%);
               -fx-background-insets: 0,1,2;
               -fx-background-radius: 4,3, 2;
               -fx-text-fill: black;""";


    public void input()
    {
        StudentRepo studentRepo = new StudentRepo();
        CourseRepo courseRepo = new CourseRepo();
        TeacherRepo teacherRepo = new TeacherRepo();
        EnrolledRepo enrolledRepo = new EnrolledRepo();
        courseContr = new CourseContr(courseRepo,studentRepo,teacherRepo,enrolledRepo);
        teacherContr = new TeacherContr(teacherRepo);
        studentContr = new StudentContr(studentRepo);
        enrolledContr =  new EnrolledContr(courseRepo,studentRepo,enrolledRepo);
    }

    @Override
    public void start(Stage stage) {
        Label label = new Label("Register as? Student or teacher?");
        label.setFont(new Font("Calibri", 16));
        Button loginTeacher = new Button();
        loginTeacher.setText("Teacher");
        loginTeacher.setStyle(settings);
        Button loginStudent = new Button();
        loginStudent.setText("Student");
        loginStudent.setStyle(settings);

        loginStudent.setOnAction(event -> studentScreen());

        loginTeacher.setOnAction(event -> teacherScreen());

        VBox box = new VBox();
        box.setPadding(new Insets(10));
        box.setStyle(background);

        box.setAlignment(Pos.BASELINE_CENTER);

        box.getChildren().add(label);
        box.getChildren().add(loginTeacher);
        box.getChildren().add(loginStudent);

        Scene scene = new Scene(box, 450, 250);

        stage.setTitle("Application student/teacher");
        stage.setScene(scene);
        stage.show();
    }


    public void incorrectLog(String user) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ID not found");
        alert.setHeaderText("Warning");
        String s = user + "doesnt exist";
        alert.setContentText(s);
        alert.show();
    }


    public void correctLogStudent(Long ID) {

        Stage stage = new Stage();
        input();
        AtomicInteger numberCredits = new AtomicInteger();
        Button button = new Button();
        button.setText("Please register");
        button.setStyle(settings);
        Label labelCourse = new Label("Enter the course ID.");
        labelCourse.setFont(new Font("Arial", 16));
        TextField textCourse = new TextField();
        textCourse.setPromptText("Course ID");

        button.setOnAction(actionEvent -> {
            String idCourse = textCourse.getText();
            long IDCourse= Long.parseLong(idCourse);
            Enrolled enrolled = new Enrolled(ID, IDCourse);
            try {
                if(enrolledContr.create(enrolled) != null)
                {
                    textCourse.clear();
                    registered();
                }
                else
                {
                    textCourse.clear();
                    notRegistered();
                }
            } catch (IOException | SQLException | AlreadyExist | EmptyArray e) {
                e.printStackTrace();
            }
        });

        Button button2 = new Button();
        button2.setText("Number of credits");
        button2.setStyle(settings);
        VBox box = new VBox();
        box.setPadding(new Insets(8));
        box.getChildren().add(button);
        box.getChildren().add(labelCourse);
        box.getChildren().add(textCourse);
        box.getChildren().add(button2);
        box.setStyle(background);
        Scene scene = new Scene(box, 450, 250);
        stage.setScene(scene);
        stage.setTitle("Students");
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 18px; -fx-font-family: 'Century';");
        button2.setOnAction(actionEvent -> {

            List<Student> listStudent = new ArrayList<>();
            try {
                listStudent = studentContr.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            for(Student student : listStudent){
                if(student.getIdStudent() == ID)
                {
                    numberCredits.set(student.getTotalCredits());
                    break;
                }
            }
            listView.getItems().clear();
            String message = "In total there are " + numberCredits + " credits";
            listView.getItems().add(message);
            box.getChildren().add(listView);
        });

        stage.show();

    }


    public void correctLogTeacher(Long ID) {
        input();
        Button button = new Button();
        button.setText("Show students");
        button.setStyle(settings);
        Stage stage = new Stage();
        input();
        VBox box = new VBox();
        box.setPadding(new Insets(4));
        Label label = new Label("Students enrolled to your course");
        label.setFont(new Font("Arial", 16));
        box.getChildren().add(label);
        box.getChildren().add(button);
        box.setStyle(background);
        Scene scene = new Scene(box, 450, 250);
        stage.setScene(scene);
        stage.setTitle("Teachers");
        ListView<String> listView = new ListView<>();
        listView.setStyle("-fx-font-size: 18px; -fx-font-family: 'Century';");
        button.setOnAction(event -> {
            listView.getItems().clear();
            List<Course> listCourse = null;
            try {
                listCourse = courseContr.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

            List<Long> teacherCourse = new ArrayList<>();
            assert listCourse != null;
            for(Course course : listCourse)
            {
                if(course.getTeacher() == ID)
                {
                    teacherCourse.add(course.getId());
                }
            }

            List<Enrolled> listEnrolled = new ArrayList<>();
            try {
                listEnrolled = enrolledContr.getAll();
            } catch (SQLException | EmptyArray | IOException e) {
                e.printStackTrace();
            }
            int nr = 0;

            for(Enrolled enrolled : listEnrolled)
            {

                if(teacherCourse.contains(enrolled.getIdCourse()))
                {
                    nr++;
                    listView.getItems().add(String.valueOf((enrolled.getIdStudent())));
                }
            }
            if(nr == 0)
            {
                Label label1 = new Label("There aren't ant students enrolled to a course.");
                label1.setFont(new Font("Calibri", 12));
                box.getChildren().add(label1);
            }
            box.getChildren().add(listView);
        });

        stage.show();
    }


    public void studentScreen() {
        Stage stage = new Stage();

        input();
        VBox box = new VBox();
        box.setPadding(new Insets(10));

        box.setStyle(background);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Enter ID");
        label.setFont(new Font("Arial", 16));

        TextField textUser = new TextField();
        textUser.setStyle(textUserStyle);
        textUser.setPromptText("ID");

        Button btnLogin = new Button();
        btnLogin.setText("Login");
        btnLogin.setStyle(settings);
        btnLogin.setOnAction(event -> {

            List<Student> listStudent = null;
            input();
            try {
                listStudent = studentContr.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            boolean found = false;
            String id = textUser.getText();
            long ID = Long.parseLong(id);
            for(Student student : Objects.requireNonNull(listStudent))
            {
                if(student.getIdStudent() == ID)
                {
                    found = true;
                    break;
                }
            }
            if(found)
            {
                correctLogStudent(ID);
            }
            else
            {
                incorrectLog(textUser.getText());
            }
            stage.close();

        });

        box.getChildren().add(label);
        box.getChildren().add(textUser);
        box.getChildren().add(btnLogin);
        Scene scene = new Scene(box, 450, 250);
        stage.setScene(scene);
        stage.setTitle("Students");
        stage.show();
    }


    public void teacherScreen() {
        Stage stage = new Stage();

        input();
        VBox box = new VBox();
        box.setPadding(new Insets(10));

        box.setStyle(background);
        box.setAlignment(Pos.CENTER);

        Label label = new Label("Enter ID.");
        label.setFont(new Font("Calibri", 16));
        TextField textUser = new TextField();
        textUser.setPromptText("ID");
        textUser.setStyle(textUserStyle);
        Button btnLogin = new Button();
        btnLogin.setText("Login");
        btnLogin.setStyle(settings);
        btnLogin.setOnAction(event -> {

            List<Teacher> listTeacher = null;
            try {
                listTeacher = teacherContr.getAll();
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
            boolean found = false;
            String id = textUser.getText();
            long ID = Long.parseLong(id);
            for(Teacher teacher : Objects.requireNonNull(listTeacher))
            {
                if(teacher.getIdTeacher() == ID)
                {
                    found = true;
                    break;
                }
            }
            if(found)
            {
                correctLogTeacher(ID);
            }
            else
            {
                incorrectLog(textUser.getText());
            }
            stage.close();

        });

        box.getChildren().add(label);
        box.getChildren().add(textUser);
        box.getChildren().add(btnLogin);
        Scene scene = new Scene(box, 450, 250);
        stage.setScene(scene);
        stage.setTitle("Teachers");
        stage.show();
    }


    public void notRegistered() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Failed registration!");
        alert.setHeaderText("Warning");
        String s = "No enrollment accomplish";
        alert.setContentText(s);
        alert.show();
    }

    public void registered() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful registration!");
        alert.setHeaderText("Success");
        String s = "Enrollment accomplished";
        alert.setContentText(s);
        alert.show();
    }

    public static void main(String[] args) {
        launch();
    }
}