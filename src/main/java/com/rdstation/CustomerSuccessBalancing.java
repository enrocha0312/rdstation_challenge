package com.rdstation;

import java.util.*;
import java.util.stream.Collectors;

public class CustomerSuccessBalancing {

    private final List<CustomerSuccess> customerSuccess;
    private final List<Customer> customers;
    private final List<Integer> customerSuccessAway;

    public CustomerSuccessBalancing(List<CustomerSuccess> customerSuccess,
                                    List<Customer> customers,
                                    List<Integer> customerSuccessAway) {
        this.customerSuccess = customerSuccess;
        this.customers = customers;
        this.customerSuccessAway = customerSuccessAway;
    }

    public int run(){
        Map<Integer, List<Customer>> pontosPorCustomers = customers.stream()
                .collect(Collectors.groupingBy(Customer::getScore));
        Map<Integer, Integer> mapaContagemPorCustomerSuccessId = new HashMap<>();
        List<CustomerSuccess> filteredCustomerSuccess = (List<CustomerSuccess>) customerSuccess.stream()
                .filter(cs -> !customerSuccessAway.contains(cs.getId()))
                .sorted(ordenarCSCrescentePorScore)
                .collect(Collectors.toList());
        int pontuacaoAnterior = Integer.MIN_VALUE;
        for (CustomerSuccess cs : filteredCustomerSuccess) {
            int finalPontuacaoAnterior = pontuacaoAnterior;
            List<Customer> customersComPontuacoesSuperiores = pontosPorCustomers.entrySet().stream()
                    .filter(entry -> entry.getKey() > finalPontuacaoAnterior && entry.getKey() <= cs.getScore())
                    .flatMap(entry -> entry.getValue().stream())
                    .collect(Collectors.toList());
            mapaContagemPorCustomerSuccessId.put(cs.getId(), customersComPontuacoesSuperiores.size());
            pontuacaoAnterior = cs.getScore();
        }
        if (mapaContagemPorCustomerSuccessId.values().stream().allMatch(v -> v == 0)) return 0;
        int maxCount = mapaContagemPorCustomerSuccessId.values().stream().max(Integer::compare).get();
        if (mapaContagemPorCustomerSuccessId.values().stream().filter(v -> v == maxCount).count() > 1) {
            return 0;
        } else {
            return mapaContagemPorCustomerSuccessId.entrySet().stream()
                    .filter(e -> e.getValue() == maxCount)
                    .findAny().get().getKey();
        }
    }
    private static Comparator ordenarCSCrescentePorScore = new Comparator<CustomerSuccess>() {
        @Override
        public int compare(CustomerSuccess cs1, CustomerSuccess cs2) {
            return Integer.compare(cs1.getScore(), cs2.getScore());
        }
    };

    private static Comparator ordenarCSCrescentePorId = new Comparator<CustomerSuccess>() {
        @Override
        public int compare(CustomerSuccess cs1, CustomerSuccess cs2) {
            return Integer.compare(cs1.getId(), cs2.getId());
        }
    };
}
