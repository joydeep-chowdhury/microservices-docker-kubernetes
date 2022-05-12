package joydeep.poc.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import joydeep.poc.configurations.AccountsServiceConfig;
import joydeep.poc.feignclients.CardsFeignClient;
import joydeep.poc.feignclients.LoansFeignClient;
import joydeep.poc.models.Accounts;
import joydeep.poc.models.Cards;
import joydeep.poc.models.Customer;
import joydeep.poc.models.CustomerDetails;
import joydeep.poc.models.Loans;
import joydeep.poc.models.Properties;
import joydeep.poc.repositories.AccountsRepository;

@RestController
public class AccountsController {

	private final AccountsRepository accountsRepository;
	private final AccountsServiceConfig accountsServiceConfig;
	private final LoansFeignClient loansFeignClient;
	private final CardsFeignClient cardsFeignClient;

	public AccountsController(final AccountsRepository accountsRepository,
			final AccountsServiceConfig accountsServiceConfig, final LoansFeignClient loansFeignClient,
			final CardsFeignClient cardsFeignClient) {
		this.accountsRepository = accountsRepository;
		this.accountsServiceConfig = accountsServiceConfig;
		this.loansFeignClient = loansFeignClient;
		this.cardsFeignClient = cardsFeignClient;
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

	@PostMapping("/myCustomerDetails")
	/*
	 * @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod =
	 * "myCustomerDetailsFallBack")
	 */
	@Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
	public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);

		return customerDetails;

	}

	private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		return customerDetails;

	}

	@GetMapping("/accounts-microservice/config")
	public String show() throws JsonProcessingException {
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsServiceConfig.getMsg(), accountsServiceConfig.getBuildVersion(),
				accountsServiceConfig.getMailDetails(), accountsServiceConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		return jsonStr;
	}
	
	@GetMapping("/sayHello")
	@RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
	public String sayHello() {
		return "Hello, Welcome to EazyBank";
	}

	private String sayHelloFallback(Throwable t) {
		return "Hi, Welcome to EazyBank";
	}

}
