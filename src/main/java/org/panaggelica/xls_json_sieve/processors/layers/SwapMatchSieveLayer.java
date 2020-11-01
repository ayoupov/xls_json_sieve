package org.panaggelica.xls_json_sieve.processors.layers;

import org.panaggelica.xls_json_sieve.model.ODHObjectDescriptor;
import org.panaggelica.xls_json_sieve.model.XLSObjectDescriptor;

import java.util.HashSet;
import java.util.Set;

public class SwapMatchSieveLayer extends AbstractSieveLayer{

    @Override
    public boolean toss(ODHObjectDescriptor o, XLSObjectDescriptor x) {
        Set<String> oSet = new HashSet<>(Set.of(o.getName().toLowerCase().split("\\s+")));
        Set<String> xSet = Set.of(x.getObjectName().toLowerCase().split("\\s+"));
        oSet.removeAll(xSet);
        return oSet.isEmpty();
    }

    @Override
    public String reason() {
        return "Swapped words";
    }
}
