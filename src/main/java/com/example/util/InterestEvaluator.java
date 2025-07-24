package com.example.util;

import com.example.model.BankAllocation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterestEvaluator {

    public double evaluateEffectiveInterest(List<BankAllocation> allocations) {
        double total = allocations.stream().mapToDouble(BankAllocation::getAmount).sum();
        if (total == 0) return 0.0;

        return allocations.stream()
                .mapToDouble(a -> a.getAmount() * a.getBank().getInterest())
                .sum() / total;
    }
}
