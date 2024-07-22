package hu.webuni.university.service;

import org.springframework.stereotype.Service;

import hu.webuni.eduservice.wsclient.SudentXmlWsImplService;
import hu.webuni.university.aspect.Retry;

@Service
@Retry(waitTime = 500, times = 5)
public class CentralEducationService {

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
}
