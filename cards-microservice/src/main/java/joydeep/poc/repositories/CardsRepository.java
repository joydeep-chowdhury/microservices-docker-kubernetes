package joydeep.poc.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import joydeep.poc.models.Cards;

@Repository
public interface CardsRepository extends CrudRepository<Cards, Long> {

	List<Cards> findByCustomerId(int customerId);

}
