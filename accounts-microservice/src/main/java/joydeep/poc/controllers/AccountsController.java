package joydeep.poc.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import joydeep.poc.models.Accounts;
import joydeep.poc.models.Customer;
import joydeep.poc.repositories.AccountsRepository;

@RestController
public class AccountsController {

	private final AccountsRepository accountsRepository;

	public AccountsController(final AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
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

}
