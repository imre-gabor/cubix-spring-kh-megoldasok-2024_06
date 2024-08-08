package hu.webuni.eduservice.jms;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import hu.webuni.eduservice.service.StudentXmlWs;
import hu.webuni.jms.dto.FreeSemesterRequest;
import hu.webuni.jms.dto.FreeSemesterResponse;
import jakarta.jms.Destination;
import jakarta.jms.Topic;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FreeSemesterRequestConsumer {

	private final JmsTemplate jmsTemplate;
	private final StudentXmlWs studentXmlWs;
	
	@JmsListener(destination = "free_semester_requests")
	public void onFreeSemesterRequest(Message<FreeSemesterRequest> request) {
		int studentId = request.getPayload().getStudentId();
		int freeSemesters = studentXmlWs.getFreeSemestersByStudent(studentId);
		
		FreeSemesterResponse response = new FreeSemesterResponse();
		response.setNumFreeSemesters(freeSemesters);
		response.setStudentId(studentId);
		
		jmsTemplate.convertAndSend((Topic) request.getHeaders().get(JmsHeaders.REPLY_TO), response);
	}
}
