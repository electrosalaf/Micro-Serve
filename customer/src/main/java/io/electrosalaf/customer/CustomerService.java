package io.electrosalaf.customer;

import io.electrosalaf.amqp.RabbitMQMessageProducer;
import io.electrosalaf.clients.fraud.FraudCheckResponse;
import io.electrosalaf.clients.fraud.FraudClient;
import io.electrosalaf.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;
    public void registerCustomer(CustomerRegistrationRequest request) throws UserAlreadyExistException {

        // TODO: check if email not taken
        Optional<Customer> optionalCustomer = customerRepository.findCustomerByEmail(request.email());

        if (optionalCustomer.isPresent()) {
            throw new UserAlreadyExistException("User Already Exist");
        }

        // TODO: check if email is valid
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        // TODO: store customer in db
        customerRepository.saveAndFlush(customer);

        // TODO: check if customer is fraudster
        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("Fraudster");
        }

        // TODO: make it async, add to the queue
        NotificationRequest notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to Microservices...", customer.getFirstName())
        );

        rabbitMQMessageProducer.publish(
                notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key"
        );
    }
}
