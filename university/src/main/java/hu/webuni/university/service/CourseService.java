package hu.webuni.university.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.QCourse;
import hu.webuni.university.repository.CourseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	
	private final CourseRepository courseRepository;
	
	@Transactional
	public List<Course> searchCourses(Predicate predicate){
		List<Course> courses = courseRepository.findAll(predicate, "Course.students", Sort.unsorted());
		courses = courseRepository.findAll(predicate, "Course.teachers", Sort.unsorted());
		return courses;
	}
	
	@Transactional
	public List<Course> searchCourses(Predicate predicate, Pageable pageable){
		List<Course> courses = courseRepository.findAll(predicate, pageable).getContent();
		List<Integer> idsOnPage = courses.stream().map(Course::getId).toList();
		//1. megoldás:
//		courses = courseRepository.findByIdInWithStudents(idsOnPage, Sort.unsorted());
//		courses = courseRepository.findByIdInWithTeachers(idsOnPage, pageable.getSort());
		
		//2. megoldás:
		BooleanExpression inByCourseId = QCourse.course.in(courses);
		courses = courseRepository.findAll(inByCourseId, "Course.students", Sort.unsorted());
		courses = courseRepository.findAll(inByCourseId, "Course.teachers", pageable.getSort());
		
		return courses;
	}
}
