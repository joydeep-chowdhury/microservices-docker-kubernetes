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

import joydeep.poc.configurations.CardsServiceConfig;
import joydeep.poc.models.Cards;
import joydeep.poc.models.Customer;
import joydeep.poc.models.Properties;
import joydeep.poc.repositories.CardsRepository;

@RestController
public class CardsController {
    private static final Logger logger =  LoggerFactory.getLogger(CardsController.class);
	private final CardsRepository cardsRepository;
	private final CardsServiceConfig cardsServiceConfig;

	public CardsController(final CardsRepository cardsRepository, final CardsServiceConfig cardsServiceConfig) {
		this.cardsRepository = cardsRepository;
		this.cardsServiceConfig = cardsServiceConfig;
	}

	@PostMapping("/myCards")
	public List<Cards> getCardDetails(@RequestBody Customer customer) {
		logger.info("Executing {} started executing api /myCards",this.getClass().getSimpleName());
		List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
		if (cards != null) {
			logger.info("Ended {} started executing api /myCards",this.getClass().getSimpleName());
			return cards;
		} else {
			logger.info("Ended {} started executing api /myCards",this.getClass().getSimpleName());
			return null;
		}

	}

	@GetMapping("/cards-microservice/config")
	public String show() throws JsonProcessingException {
		logger.info("Executing {} started executing api /cards-microservice/config",this.getClass().getSimpleName());
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		Properties properties = new Properties(cardsServiceConfig.getMsg(), cardsServiceConfig.getBuildVersion(),
				cardsServiceConfig.getMailDetails(), cardsServiceConfig.getActiveBranches());
		String jsonStr = ow.writeValueAsString(properties);
		logger.info("Ended {} started executing api /cards-microservice/config",this.getClass().getSimpleName());
		return jsonStr;
	}

}
