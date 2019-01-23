package se.ericsson.cf.scott.sandbox.twin;

import com.google.common.collect.ImmutableSet;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.lyo.oslc4j.core.exception.OslcCoreApplicationException;
import org.eclipse.lyo.oslc4j.core.model.ServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ericsson.cf.scott.sandbox.twin.servlet.TwinsServiceProvidersFactory;

/**
 * TBD
 *
 * @since FIXME
 */
public class TwinRepositoryFakeImpl implements TwinRepository {
    private final static Logger log = LoggerFactory.getLogger(TwinRepositoryFakeImpl.class);
    private final Set<ServiceProvider> serviceProviders = new HashSet<>();

    public TwinRepositoryFakeImpl() {
        // FIXME Andrew@2019-01-23: use a factory method
        final TwinsServiceProviderInfo twinInfo = new TwinsServiceProviderInfo("Fake Twin", "robot", "r-1");
        try {
            serviceProviders.addAll(ImmutableSet.of(TwinsServiceProvidersFactory.createTwinsServiceProvider(twinInfo)));
        } catch (OslcCoreApplicationException | URISyntaxException e) {
            log.error("Can't init SP TwinsServiceProviderInfo", e);
        }
    }

    @Override
    public Collection<ServiceProvider> getServiceProviders() {
        // TODO Andrew@2019-01-23: use SPCSingleton's method
        return ImmutableSet.copyOf(serviceProviders);
    }

    @Override
    public ServiceProvider[] asSpArray() {
        return getServiceProviders().toArray(new ServiceProvider[0]);
    }

    @Override
    public ServiceProvider registerTwinSP(final TwinsServiceProviderInfo spInfo) {
        // TODO Andrew@2019-01-23: use SPCSingleton's method
        throw new UnsupportedOperationException("Not implemented yet");
//        return null;
    }
}
