package br.ufsc.ppgcc.experion.view.expertise.builder.engine;

import br.ufsc.ppgcc.experion.model.evidence.LogicalEvidence;
import br.ufsc.ppgcc.experion.view.expertise.Expertise;

import java.util.*;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

/**
 * Standard expertise builder - just concatenates the logical evidences
 *
 * @author Rodrigo Gonçalves
 * @version 2019-03-05 - First version
 *
 */
public class StandardWeightedExpertiseBuilderEngine implements ExpertiseBuilderEngine {

    public Set<Expertise> buildExpertise(Set<LogicalEvidence> evidenceList) {

        Map<LogicalEvidence, Integer> weights = new HashMap<>();

        for (LogicalEvidence evidence : evidenceList) {
            weights.put(evidence, evidence.getPhysicalEvidences().size());
        }

        Map<LogicalEvidence, Integer> sorted = weights
                .entrySet()
                .stream()
                .sorted(comparingByValue(new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return (o1 - o2) * -1;
                    }
                }))
                .collect(
                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                                LinkedHashMap::new));

        Expertise profile = new Expertise();
        profile.setEvidences(evidenceList);

        String description = "";
        for (LogicalEvidence evidence : sorted.keySet()) {
            description += "[" + sorted.get(evidence) + "] => " + evidence.getConcept() + "\n";
        }

        profile.setDescription(description);


        Set<Expertise> result = new HashSet<>();
        result.add(profile);
        return result;
    }
}
