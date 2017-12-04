package com.tfl.billing;

import com.tfl.external.Customer;
import com.tfl.external.CustomerDatabase;

import java.util.List;
import java.util.UUID;

public class CustomerDatabaseAdapter implements Database {
    private static CustomerDatabaseAdapter instance= new CustomerDatabaseAdapter();
    private CustomerDatabase customerDatabase = CustomerDatabase.getInstance();


    public static CustomerDatabaseAdapter getInstance()
    {
        return instance;
    }

    @Override
    public List<Customer> getCustomers()
    {
        return customerDatabase.getCustomers();
    };

    @Override
    public boolean isRegisteredId(UUID cardId)
    {
        return customerDatabase.isRegisteredId(cardId);
    }
}
