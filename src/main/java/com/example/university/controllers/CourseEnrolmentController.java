package com.example.university.controllers;


import com.example.university.dto.CourseDTO;
import com.example.university.dto.CourseEnrolmentDTO;
import com.example.university.models.Course;
import com.example.university.queryresults.CourseEnrolmentQueryResult;
import com.example.university.requests.CourseEnrolmentRequest;
import com.example.university.services.CourseEnrolmentService;
import com.example.university.services.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/v1/enrollments")
public class CourseEnrolmentController {
    private final CourseEnrolmentService courseEnrolmentService;
    private final LessonService lessonService;

    public CourseEnrolmentController(CourseEnrolmentService courseEnrolmentService, LessonService lessonService) {
        this.courseEnrolmentService = courseEnrolmentService;
        this.lessonService = lessonService;
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseDTO>> enrollments(Principal principal) {
        List<Course> courses = courseEnrolmentService.getAllEnrolledCoursesByUsername(principal.getName());

        List<CourseDTO> responseCourses = courses.stream().map((course) -> {
            CourseDTO responseCourse = new CourseDTO();

            responseCourse.setIdentifier(course.getIdentifier());
            responseCourse.setTitle(course.getTitle());
            responseCourse.setTeacher(course.getTeacher());
            responseCourse.setLessons(lessonService.getAllLessonsByCourseIdentifier(course.getIdentifier()));
            responseCourse.setEnrolled(true);

            return responseCourse;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(responseCourses, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<CourseEnrolmentDTO> enrollIn(@RequestBody CourseEnrolmentRequest request, Principal principal) {
        CourseEnrolmentQueryResult enrolment = courseEnrolmentService.enrollIn(principal.getName(), request.getCourseIdentifier());

        CourseEnrolmentDTO responseEnrolment = new CourseEnrolmentDTO();

        responseEnrolment.setName(enrolment.getUser().getName());
        responseEnrolment.setUsername(enrolment.getUser().getUsername());
        responseEnrolment.setCourse(enrolment.getCourse());

        return new ResponseEntity<>(responseEnrolment, HttpStatus.OK);
    }
}