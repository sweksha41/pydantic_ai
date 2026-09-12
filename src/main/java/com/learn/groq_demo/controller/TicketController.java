package com.learn.groq_demo.controller;

import com.learn.groq_demo.model.Ticket;
import com.learn.groq_demo.service.TicketService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/groq")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/extract-ticket")
    public Mono<Ticket> extractTicket(@RequestParam String text) {
        return ticketService.extractTicket(text);
    }
}