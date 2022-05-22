package joydeep.poc.feignclients;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import joydeep.poc.models.Customer;
import joydeep.poc.models.Loans;

@FeignClient("loans-microservice")
public interface LoansFeignClient {
	@RequestMapping(method = RequestMethod.POST, value = "myLoans", consumes = "application/json")
	List<Loans> getLoansDetails(@RequestBody Customer customer);
}
