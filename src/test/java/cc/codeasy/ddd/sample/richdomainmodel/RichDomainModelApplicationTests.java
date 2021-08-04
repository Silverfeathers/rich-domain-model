package cc.codeasy.ddd.sample.richdomainmodel;

import cc.codeasy.ddd.sample.richdomainmodel.model.Account;
import cc.codeasy.ddd.sample.richdomainmodel.model.Amount;
import cc.codeasy.ddd.sample.richdomainmodel.model.Owner;
import cc.codeasy.ddd.sample.richdomainmodel.model.Source;
import cc.codeasy.ddd.sample.richdomainmodel.repository.AccountRepository;
import cc.codeasy.ddd.sample.richdomainmodel.service.AccountService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@SpringBootTest
class RichDomainModelApplicationTests {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountService accountService;

	@Test
	void accountRepository() {
		Account account = new Account(new Owner("tom"), new Amount(99));
		accountRepository.save(account);

		Source source = new Source("o-2020-0001", 1);

		Iterable<Account> acs = accountRepository.findAll();
		acs.forEach(System.out::println);

		System.out.println("充值1");
		Account ac1 = accountRepository.findById(1L);
		ac1.recharge(new Amount(1), source);
		accountRepository.save(ac1);
		ac1 = accountRepository.findById(1L);
		System.out.println(ac1);
		ac1.getRechargeRecords().forEach(System.out::println);


		System.out.println("消费50");
		ac1.consume(new Amount(50), source);
		accountRepository.save(ac1);
		ac1 = accountRepository.findById(1L);
		System.out.println(ac1);
		ac1.getConsumeRecords().forEach(System.out::println);

	}


	@Test
	void accountService() {

		Account account = new Account(new Owner("tom"), new Amount(99));
		accountRepository.save(account);


		AccountService.RechargeRequest request = new AccountService.RechargeRequest();
		request.setAmount(200);
		request.setSourceId("o-2020-0001");
		request.setSourceType(1);
		request.setUserId("tom");

		AccountService.RechargeResponse resp = accountService.recharge(request);
		System.out.println(resp);
		assertThat(resp.isSuccess()).isTrue();

		resp = accountService.recharge(request);
		System.out.println(resp);
		assertThat(resp.isSuccess()).isFalse();
		assertThat(resp.getErrorCode()).isEqualTo(AccountService.RechargeResponse.DUPLICATE_RECHARGE);

	}
}
