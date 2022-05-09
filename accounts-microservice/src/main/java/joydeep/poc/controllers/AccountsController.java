package joydeep.poc.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import joydeep.poc.configurations.AccountsServiceConfig;
import joydeep.poc.models.Accounts;
import joydeep.poc.models.Customer;
import joydeep.poc.models.Properties;
import joydeep.poc.repositories.AccountsRepository;

@RestController
public class AccountsController {

	private final AccountsRepository accountsRepository;
	private final AccountsServiceConfig accountsServiceConfig;

	public AccountsController(final AccountsRepository accountsRepository,
			final AccountsServiceConfig accountsServiceConfig) {
		this.accountsRepository = accountsRepository;
		this.accountsServiceConfig = accountsServiceConfig;
	}

	@PostMapping("/myAccount")
	public Accounts getAccountsDetails(@RequestBody Customer customer) {
		Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (account != null) {
			return account;
		} else {
			return null;
		}
	}

	@GetMapping("/accounts-microservice/config")
	public String show() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsServiceConfig.getMsg(), accountsServiceConfig.getBuildVersion(),
				accountsServiceConfig.getMailDetails(), accountsServiceConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}

}
