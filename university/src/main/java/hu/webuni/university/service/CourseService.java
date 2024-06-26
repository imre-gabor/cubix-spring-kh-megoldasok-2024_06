package hu.webuni.university.service;

import java.util.List;

import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import hu.webuni.university.model.Course;
import hu.webuni.university.model.HistoryData;
import hu.webuni.university.model.QCourse;
import hu.webuni.university.repository.CourseRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {
	
	private final CourseRepository courseRepository;
	
	@PersistenceContext
	private EntityManager em;
	
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
	@Transactional
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<HistoryData<Course>> getHistoryById(int id) {

		List resultList = AuditReaderFactory.get(em)
			.createQuery()
			.forRevisionsOfEntity(Course.class, false, true)
			.add(AuditEntity.property("id").eq(id))
			.getResultList().stream().map(o -> {
					Object[] objArray = (Object[]) o;
					
					DefaultRevisionEntity defaultRevisionEntity = (DefaultRevisionEntity) objArray[1];
					RevisionType revType = (RevisionType) objArray[2];
					
					Course course = (Course) objArray[0];
					course.getStudents().size();
					course.getTeachers().size();
					
					HistoryData<Course> historyData = 
						new HistoryData<>(
							course, revType,
							defaultRevisionEntity.getId(), defaultRevisionEntity.getRevisionDate());
					return historyData;
				}).toList();
		return resultList;
	}


}
