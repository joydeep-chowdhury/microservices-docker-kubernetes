package joydeep.poc.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import joydeep.poc.configurations.LoansServiceConfig;
import joydeep.poc.models.Customer;
import joydeep.poc.models.Loans;
import joydeep.poc.models.Properties;
import joydeep.poc.repositories.LoansRepository;

@RestController
public class LoansController {

	private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
	private final LoansRepository loansRepository;
	private final LoansServiceConfig loansConfig;
	

	public LoansController(final LoansRepository loansRepository, final LoansServiceConfig loansConfig) {
		this.loansRepository = loansRepository;
		this.loansConfig = loansConfig;
	}

	@PostMapping("/myLoans")
	public List<Loans> getLoansDetails(@RequestBody Customer customer) {
		logger.info("Executing {} started executing api /myLoans",this.getClass().getSimpleName());
		List<Loans> loans = loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
		if (loans != null) {
			logger.info("Executing {} ended executing api /myLoans",this.getClass().getSimpleName());
			return loans;
		} else {
			logger.info("Executing {} ended executing api /myLoans",this.getClass().getSimpleName());
			return null;
		}

	}
	
	@GetMapping("/loans-microservice/config")
	public String show() throws JsonProcessingException {
		logger.info("Executing {} started executing api /loans-microservice/config",this.getClass().getSimpleName());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(loansConfig.getMsg(), loansConfig.getBuildVersion(),
				loansConfig.getMailDetails(), loansConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		logger.info("Executing {} ended executing api /loans-microservice/config",this.getClass().getSimpleName());
		return jsonStr;
	}

}
