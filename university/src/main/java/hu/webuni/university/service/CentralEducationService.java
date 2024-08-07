package hu.webuni.university.service;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import hu.webuni.eduservice.wsclient.SudentXmlWsImplService;
import hu.webuni.jms.dto.FreeSemesterRequest;
import hu.webuni.university.aspect.Retry;
import jakarta.jms.Destination;
import lombok.RequiredArgsConstructor;

@Service
@Retry(waitTime = 500, times = 5)
@RequiredArgsConstructor
public class CentralEducationService {
	
	private final JmsTemplate educationJmsTemplate;

//	private Random random = new Random();

	public int getNumFreeSemestersForStudent(int eduId) {
		return new SudentXmlWsImplService().getSudentXmlWsImplPort().getFreeSemestersByStudent(eduId);
		
//		int rnd = random.nextInt(0, 2);
//		if (rnd == 0) {
//			throw new RuntimeException("Central Education Service timed out.");
//		} else {
//			return random.nextInt(0, 10);
//		}
	}

	public void askNumFreeSemestersForStudent(int eduId) {
		FreeSemesterRequest freeSemesterRequest = new FreeSemesterRequest();
		freeSemesterRequest.setStudentId(eduId);
		
		Destination topic = educationJmsTemplate.execute(session -> session.createTopic("free_semester_responses"));
		
		educationJmsTemplate.convertAndSend("free_semester_requests", freeSemesterRequest, message -> {
			message.setJMSReplyTo(topic);
			return message;
		});
	}
}
