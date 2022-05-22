package joydeep.poc.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
	private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);
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
		logger.info("Executing {} started executing api /myAccount ", this.getClass().getSimpleName());
		Accounts account = accountsRepository.findByCustomerId(customer.getCustomerId());
		if (account != null) {
			logger.info("Executing {} ended executing api /myAccount ", this.getClass().getSimpleName());
			return account;
		} else {
			logger.info("Executing {} ended executing api /myAccount ", this.getClass().getSimpleName());
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
		logger.info("Executing {} started executing api /myCustomerDetails", this.getClass().getSimpleName());
		Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
		List<Loans> loans = loansFeignClient.getLoansDetails(customer);
		List<Cards> cards = cardsFeignClient.getCardDetails(customer);

		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setAccounts(accounts);
		customerDetails.setLoans(loans);
		customerDetails.setCards(cards);
		logger.info("Executing {} ended executing api /myCustomerDetails ", this.getClass().getSimpleName());
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
		logger.info("Executing {} started executing api /accounts-microservice/config",this.getClass().getSimpleName());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(accountsServiceConfig.getMsg(), accountsServiceConfig.getBuildVersion(),
				accountsServiceConfig.getMailDetails(), accountsServiceConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		logger.info("Executing {} ended executing api /accounts-microservice/config",this.getClass().getSimpleName());
		return jsonStr;
	}

	@GetMapping("/sayHello")
	@RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
	public String sayHello() {
		logger.info("Executing {} started executing api /sayHello",this.getClass().getSimpleName());
		return "Hello, Welcome to EazyBank";
	}

	private String sayHelloFallback(Throwable t) {
		logger.info("Executing {} started executing method {} ",this.getClass().getSimpleName(),"sayHelloFallback");
		return "Hi, Welcome to EazyBank";
	}

}
