package socklib.test;

import org.junit.rules.ExternalResource;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Rule that provides an unused port.
 */
public class UnitPort extends ExternalResource {

    private Integer port = null;
    private boolean terminated = false;

    public synchronized int get() throws IOException {
        checkState(!terminated, "this instance be reused");
        if (port == null) {
            port = Ports.unused();
        }
        return port;
    }

    @Override
    protected synchronized void after() {
        terminated = true;
        port = null;
    }
}
