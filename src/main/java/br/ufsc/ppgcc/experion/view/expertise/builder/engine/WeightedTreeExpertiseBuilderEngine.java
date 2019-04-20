package br.ufsc.ppgcc.experion.view.expertise.builder.engine;

import br.ufsc.ppgcc.experion.model.evidence.LogicalEvidence;
import br.ufsc.ppgcc.experion.view.expertise.Expertise;
import org.apache.commons.lang3.StringUtils;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.util.SupplierUtil;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;

/**
 * Weighted expertise builder - weight based on how many times the evidence appears
 *
 * @author Rodrigo Gonçalves
 * @version 2019-03-05 - First version
 *
 */
public class WeightedTreeExpertiseBuilderEngine implements ExpertiseBuilderEngine {

    private class ProfileTreeNode {
        public String label;
        public LogicalEvidence evidence;

        @Override
        public String toString() {
            return label;
        }

        public ProfileTreeNode(String label) {
            this.label = label;
            this.evidence = evidence;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProfileTreeNode that = (ProfileTreeNode) o;
            return Objects.equals(label, that.label);
        }

        @Override
        public int hashCode() {
            return Objects.hash(label);
        }
    }

    public Set<Expertise> buildExpertise(Set<LogicalEvidence> evidenceList) {

        Map<String, Integer> weights = new HashMap<>();

        for (LogicalEvidence evidence : evidenceList) {
            if (weights.containsKey(evidence.getConcept())) {
                weights.replace(evidence.getConcept(), weights.get(evidence.getConcept()) + evidence.getPhysicalEvidences().size());
            } else {
                weights.put(evidence.getConcept(), evidence.getPhysicalEvidences().size());
            }
        }

        List<Integer> sortedWeights = weights.values().stream().distinct().sorted((a,b) -> b - a).collect(Collectors.toList());
        sortedWeights = sortedWeights.subList(0, Math.min(sortedWeights.size(), sortedWeights.size() / 2 + 1));
//
//        Map<String, Integer> sorted = weights
//                .entrySet()
//                .stream()
//                .distinct()
//                .sorted(comparingByValue(new Comparator<Integer>() {
//                    @Override
//                    public int compare(Integer o1, Integer o2) {
//                        return (o1 - o2) * -1;
//                    }
//                }))
//                .collect(
//                        toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
//                                LinkedHashMap::new));




//        Map<String, Integer> weighted = new HashMap<>();
//
//        for (LogicalEvidence evidence : sorted.keySet()) {
//            weighted.put(evidence.getConcept(), sorted.get(evidence));
//        }


        Expertise profile = new Expertise();
        profile.setEvidences(evidenceList);

//        List<Integer> values = new LinkedList<>();
//        values.addAll(sorted.values());
//        values.sort(new Comparator<Integer>() {
//            @Override
//            public int compare(Integer o1, Integer o2) {
//                return o1.compareTo(o2) * -1;
//            }
//        });

        String fullDescription = "";
//        Integer lastWeight = -1;
        for (Integer weight : sortedWeights) {
//            if (weight.equals(lastWeight)) {
//                continue;
//            }
//            lastWeight = weight;
            fullDescription += "\n\nWeight: " + weight + "\n-----------------\n";


            DirectedAcyclicGraph<ProfileTreeNode, DefaultWeightedEdge> graph = new DirectedAcyclicGraph<ProfileTreeNode, DefaultWeightedEdge>((Supplier)null, SupplierUtil.createSupplier(DefaultWeightedEdge.class), true);

            for (String evidence : weights.keySet()) {
                if (weights.get(evidence).equals(weight)) {
                    String[] elements = StringUtils.splitByWholeSeparator(evidence, " -> ");

                    ProfileTreeNode previousElement = null;
                    for (String element : elements) {
                        ProfileTreeNode node = new ProfileTreeNode(element);

                        if (previousElement == null) {
                            if (!graph.containsVertex(node)) {
                                graph.addVertex(node);
                            }
                        } else {
                            if (!graph.containsVertex(node)) {
                                graph.addVertex(node);
                            }
                            if (!graph.containsEdge(previousElement, node)) {
                                graph.setEdgeWeight(graph.addEdge(previousElement, node), 1);
                            } else {
                                DefaultWeightedEdge edge = graph.getEdge(previousElement, node);
                                graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
                            }
                        }
                        previousElement = node;
                    }
                }
            }

            DepthFirstIterator<ProfileTreeNode, DefaultWeightedEdge> iterator = new DepthFirstIterator<>(graph);
            for (; iterator.hasNext(); ) {
                ProfileTreeNode concept = iterator.next();
                Integer level = graph.getAncestors(concept).size();
                fullDescription += StringUtils.repeat("\t", level) + concept;
//                if (graph.getDescendants(concept).isEmpty()) {
//                    fullDescription += " => " + sorted.get(concept.evidence) + "\n";
//                } else {
                    fullDescription += "\n";
//                }
            }

        }

        profile.setDescription(fullDescription);

        Set<Expertise> result = new HashSet<>();
        result.add(profile);
        return result;
    }
}
