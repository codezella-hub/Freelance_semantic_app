package tn.esprit.freelance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import tn.esprit.freelance.service.RdfService;
import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.freelance.service.AdvancedSearchService;

@RestController
@RequestMapping("/api/rdf")
@CrossOrigin(origins = "http://localhost:4200")
public class RdfController {

    @Autowired
    private RdfService rdfService;

    @Autowired
    private AdvancedSearchService advancedSearchService;

    @GetMapping(value = "/contracts", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getContracts() {
        return rdfService.getContracts();
    }

    @GetMapping(value = "/payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getPayments() {
        return rdfService.getPayments();
    }

    @GetMapping(value = "/search/payments", produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchPayments(@RequestParam String query) {
        return rdfService.searchPayments(query);
    }

    @GetMapping(value = "/search/semantic", produces = MediaType.APPLICATION_JSON_VALUE)
    public String semanticSearch(
            @RequestParam(required = false, name = "titleQuery") String titleQuery,
            @RequestParam(required = false, name = "minAmount") Double minAmount,
            @RequestParam(required = false, name = "status") String status
    ) {
        // Provide defaults if not supplied
        String tq = titleQuery == null ? "" : titleQuery;
        double ma = minAmount == null ? 0.0 : minAmount.doubleValue();
        String st = status == null ? "" : status;
        return advancedSearchService.searchContractsAndPayments(tq, ma, st);
    }

}
