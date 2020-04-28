package jpabook.jpashop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AppRunner implements ApplicationRunner {

	@Autowired
	private TransactionService ts;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println("=== App1 =======");
		ts.outerTransaction();
		System.out.println("==================");
	}

}
