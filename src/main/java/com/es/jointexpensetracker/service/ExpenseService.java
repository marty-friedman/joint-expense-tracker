package com.es.jointexpensetracker.service;

import com.es.jointexpensetracker.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ExpenseService {
    private static final ExpenseService instance = new ExpenseService();
    private Map<Long, Expense> expenses;
    private AtomicLong nextId;
    private String expenseGroup;

    private ExpenseService() {
        expenseGroup = UUID.randomUUID().toString();
        expenses = Stream.of(
                new Expense(1L, "Train tickets from Minsk to Warsaw", new BigDecimal(200), "Andrei", expenseGroup),
                new Expense(2L, "Air tickets from Warsaw to Gran Carania and back", new BigDecimal(2000), "Ivan", expenseGroup),
                new Expense(3L, "Restaurant", new BigDecimal(90), "Andrei", expenseGroup),
                new Expense(4L, "Rent a car", new BigDecimal(700), "Sergei", expenseGroup),
                new Expense(5L, "Rent a car", new BigDecimal(500), "Igor", expenseGroup),
                new Expense(6L, "Rent a house", new BigDecimal(2000), "Igor", expenseGroup),
                new Expense(7L, "Restaurant", new BigDecimal(60), "Andrei", expenseGroup),
                new Expense(8L, "Gazoline", new BigDecimal(50), "Sergei", expenseGroup),
                new Expense(9L, "Gazoline", new BigDecimal(50), "Igor", expenseGroup),
                new Expense(10L, "Surfing", new BigDecimal(30), "Sergei", expenseGroup),
                new Expense(11L, "New year party shopping", new BigDecimal(30), "Igor", expenseGroup),
                new Expense(12L, "Surfing", new BigDecimal(30), "Sergei", expenseGroup),
                new Expense(13L, "Air wing", new BigDecimal(50), "Sergei", expenseGroup),
                new Expense(14L, "Bus tickets from Warsaw to Minsk", new BigDecimal(200), "Andrei", expenseGroup)
        ).collect(Collectors.toMap(Expense::getId, Function.identity(), (one, two)->one, ConcurrentHashMap::new));
        nextId = new AtomicLong(15L);
    }

    public static ExpenseService getInstance(){
        return instance;
    }

    public Collection<Expense> getExpenses(){
        return Collections.unmodifiableCollection(expenses.values());
    }

    public Expense getExpenseById(long id){
        return expenses.get(id);
    }

    public void removeExpense(Expense expense){
        expenses.remove(expense.getId());
    }

    public Expense addExpense(String person, String description, BigDecimal amount, Currency currency, LocalDate date){
        long id = nextId.getAndIncrement();
        Expense expense = new Expense(id, description, amount, currency, person, date, expenseGroup);
        expenses.put(id, expense);
        return expense;
    }
}
