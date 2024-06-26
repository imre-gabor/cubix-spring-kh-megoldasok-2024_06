package hu.webuni.university.repository;


import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import com.querydsl.core.types.dsl.StringExpression;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.QCourse;
import jakarta.persistence.NamedEntityGraph;

public interface CourseRepository extends JpaRepository<Course, Integer>, 
										QuerydslPredicateExecutor<Course>,
										QuerydslBinderCustomizer<QCourse>,
										QuerydslWithEntityGraphRepository<Course, Integer>{

	@EntityGraph("Course.students")
	@Query("SELECT c From Course c WHERE c.id IN :ids")
	List<Course> findByIdInWithStudents(List<Integer> ids, Sort sort);
	
	@Query("SELECT c From Course c WHERE c.id IN :ids")
	@EntityGraph("Course.teachers")
	List<Course> findByIdInWithTeachers(List<Integer> ids, Sort sort);
	
	
	@Override
	default void customize(QuerydslBindings bindings, QCourse course) {
		bindings.bind(course.name).first(StringExpression::startsWithIgnoreCase);
		bindings.bind(course.teachers.any().name).first(StringExpression::startsWithIgnoreCase);
		bindings.bind(course.students.any().semester).all((path, values) -> {
			if(values.size() != 2)
				return Optional.empty();
			
			Iterator<? extends Integer> iterator = values.iterator();
			Integer from = iterator.next();
			Integer to = iterator.next();
			
			return Optional.of(path.between(from, to));
		});
	}
	List<Course> findByName(String name);
}
