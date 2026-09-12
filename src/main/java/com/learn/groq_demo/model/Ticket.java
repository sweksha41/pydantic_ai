package com.learn.groq_demo.model;

public record Ticket(
        String name,
        String email,
        String issue,
        String error
) {}