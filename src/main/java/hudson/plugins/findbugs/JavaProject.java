package hudson.plugins.findbugs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// CHECKSTYLE:OFF
public class JavaProject {
    private final List<Module> modules = new ArrayList<Module>();

    public final void addModule(final Module module) {
        modules.add(module);
    }

    public Collection<Module> getModules() {
        return Collections.unmodifiableCollection(modules);
    }

    public int getNumberOfWarnings() {
        int warnings = 0;
        for (Module module : modules) {
            warnings += module.getNumberOfWarnings();
        }
        return warnings;
    }

    /**
     * Returns the number of warnings of the specified package.
     *
     * @param packageName
     *            the package to return the warnings for
     * @return number of warnings of the specified package.
     */
    public int getNumberOfWarnings(final String packageName) {
        int warnings = 0;
        for (Module module : modules) {
            warnings += module.getNumberOfWarnings(packageName);
        }
        return warnings;
    }

    public Collection<Warning> getWarnings(final String packageName) {
        ArrayList<Warning> warnings = new ArrayList<Warning>();
        for (Module module : modules) {
            warnings.addAll(module.getWarnings(packageName));
        }
        return warnings;
    }

    public Set<Warning> getWarnings() {
        Set<Warning> warnings = new HashSet<Warning>();
        for (Module module : modules) {
            warnings.addAll(module.getWarnings());
        }
        return warnings;
    }
}
