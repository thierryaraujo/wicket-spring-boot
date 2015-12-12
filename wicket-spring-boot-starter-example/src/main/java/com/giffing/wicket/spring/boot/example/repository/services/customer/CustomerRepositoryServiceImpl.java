package com.giffing.wicket.spring.boot.example.repository.services.customer;


import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.giffing.wicket.spring.boot.example.model.Customer;
import com.giffing.wicket.spring.boot.example.repository.services.customer.filter.CustomerFilter;
import com.giffing.wicket.spring.boot.example.repository.services.customer.specs.CustomerSpecs;

@Component
@Transactional(readOnly=true)
public class CustomerRepositoryServiceImpl implements CustomerRepositoryService {

	private CustomerRepository customerRepository;

	@Inject
	public CustomerRepositoryServiceImpl(CustomerRepository customerRepository){
		this.customerRepository = customerRepository;
	}
	
	
	@Override
	public List<Customer> findAll(Long page, Long count, CustomerFilter filter) {
		Sort sort = null;
		if(filter.sort() != null){
			Direction direction = filter.isAscending() ? Direction.ASC : Direction.DESC;
			sort = new Sort(direction, filter.sort().getSortName());
		}
		Pageable pageable = new PageRequest(page.intValue(), count.intValue(), sort );
		return customerRepository.findAll(filter(filter), pageable).getContent();
	}

	@Override
	public long count(CustomerFilter filter) {
		return customerRepository.count(filter(filter));
	}
	
	private Specification<Customer> filter(CustomerFilter filter) {
		List<Specification<Customer>> specs = new ArrayList<>();
		
		if(filter.getId() != null){
			specs.add(CustomerSpecs.hasId(filter.getId()));
		}
		
		if(isNotEmpty(filter.getUsername())){
			specs.add(CustomerSpecs.hasUsername(filter.getUsername()));
		}
		
		Specifications<Customer> spec = null;
		for (Specification<Customer> specification : specs) {
			if(spec == null){
				spec = Specifications.where(specification);
			}else {
				spec = spec.and(specification);
			}
		}
		
		return spec;
	}
	
	boolean isNotEmpty(String toCheck){
		if(toCheck != null && toCheck.length() > 0){
			return true;
		}
		
		return false;
	}


	
}
