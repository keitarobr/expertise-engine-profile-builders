package br.ufsc.ppgcc.experion.view.profile.builder.engine;

import br.ufsc.ppgcc.experion.view.expertise.Expertise;
import br.ufsc.ppgcc.experion.view.profile.Profile;

import java.util.HashSet;
import java.util.Set;

/**
 * Standard profile builder - just concatenates the expertise information
 *
 * @author Rodrigo Gonçalves
 * @version 2019-03-05 - First version
 *
 */
public class StandardProfileBuilder implements ProfileBuilderEngine {

    public Set<Profile> buildProfiles(Set<Expertise> expertise) {
        Profile profile = new Profile();

        StringBuilder str = new StringBuilder("");

        for (Expertise exp : expertise) {
            profile.getExpertise().add(exp);
            str.append(exp.getDescription()).append("\n\n");
            profile.setExpert(exp.getExpert());
        }

        profile.setDescription(str.toString());

        Set<Profile> result = new HashSet<Profile>();
        result.add(profile);

        return result;
    }
}
