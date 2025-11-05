package tn.esprit.freelance.validation;

import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class RdfValidationService {

    private static final String FREELANCE_NS = "http://www.semanticweb.org/monpc/ontologies/2025/9/untitled-ontology-8#";

    public List<String> validateContract(Model model, Resource contract) {
        List<String> errors = new ArrayList<>();

        // Check required properties
        validateRequiredProperty(model, contract, FREELANCE_NS + "title", errors, "Contract title is required");
        validateRequiredProperty(model, contract, FREELANCE_NS + "description", errors, "Contract description is required");
        validateRequiredProperty(model, contract, FREELANCE_NS + "startDate", errors, "Contract start date is required");
        validateRequiredProperty(model, contract, FREELANCE_NS + "endDate", errors, "Contract end date is required");
        validateRequiredProperty(model, contract, FREELANCE_NS + "amount", errors, "Contract amount is required");
        validateRequiredProperty(model, contract, FREELANCE_NS + "status", errors, "Contract status is required");

        // Validate dates
        Property startDateProp = model.createProperty(FREELANCE_NS + "startDate");
        Property endDateProp = model.createProperty(FREELANCE_NS + "endDate");

        if (contract.hasProperty(startDateProp) && contract.hasProperty(endDateProp)) {
            LocalDate startDate = LocalDate.parse(contract.getProperty(startDateProp).getString());
            LocalDate endDate = LocalDate.parse(contract.getProperty(endDateProp).getString());

            if (endDate.isBefore(startDate)) {
                errors.add("Contract end date cannot be before start date");
            }
        }

        // Validate amount
        Property amountProp = model.createProperty(FREELANCE_NS + "amount");
        if (contract.hasProperty(amountProp)) {
            double amount = contract.getProperty(amountProp).getDouble();
            if (amount <= 0) {
                errors.add("Contract amount must be greater than 0");
            }
        }

        return errors;
    }

    public List<String> validatePayment(Model model, Resource payment) {
        List<String> errors = new ArrayList<>();

        // Check required properties
        validateRequiredProperty(model, payment, FREELANCE_NS + "amount", errors, "Payment amount is required");
        validateRequiredProperty(model, payment, FREELANCE_NS + "paymentDate", errors, "Payment date is required");
        validateRequiredProperty(model, payment, FREELANCE_NS + "status", errors, "Payment status is required");
        validateRequiredProperty(model, payment, FREELANCE_NS + "paymentMethod", errors, "Payment method is required");

        // Validate amount
        Property amountProp = model.createProperty(FREELANCE_NS + "amount");
        if (payment.hasProperty(amountProp)) {
            double amount = payment.getProperty(amountProp).getDouble();
            if (amount <= 0) {
                errors.add("Payment amount must be greater than 0");
            }
        }

        // Validate payment date
        Property paymentDateProp = model.createProperty(FREELANCE_NS + "paymentDate");
        if (payment.hasProperty(paymentDateProp)) {
            LocalDateTime paymentDate = LocalDateTime.parse(payment.getProperty(paymentDateProp).getString());
            if (paymentDate.isAfter(LocalDateTime.now())) {
                errors.add("Payment date cannot be in the future");
            }
        }

        // Validate payment method
        Property methodProp = model.createProperty(FREELANCE_NS + "paymentMethod");
        if (payment.hasProperty(methodProp)) {
            String method = payment.getProperty(methodProp).getString();
            if (!isValidPaymentMethod(method)) {
                errors.add("Invalid payment method: " + method);
            }
        }

        return errors;
    }

    private void validateRequiredProperty(Model model, Resource resource, String propertyUri, 
                                        List<String> errors, String errorMessage) {
        Property property = model.createProperty(propertyUri);
        if (!resource.hasProperty(property)) {
            errors.add(errorMessage);
        }
    }

    private boolean isValidPaymentMethod(String method) {
        return method != null && (
            method.equals("CREDIT_CARD") ||
            method.equals("BANK_TRANSFER") ||
            method.equals("PAYPAL") ||
            method.equals("CRYPTO")
        );
    }
}