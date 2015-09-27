package fucksocks.common.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 21, 2015 2:31 PM
 */
public class MonitorInputStreamWrapper extends InputStream {

  private InputStream originalInputStream;
  private List<InputStreamMonitor> monitors;
  private InputStream inputStream;
  private OutputStream outputStream;

  public MonitorInputStreamWrapper(InputStream inputStream) {
    this.originalInputStream = inputStream;
  }

  public MonitorInputStreamWrapper(InputStream inputStream, InputStreamMonitor... monitors) {
    this.originalInputStream = inputStream;
    this.monitors = new ArrayList<>(monitors.length);
  }

  public MonitorInputStreamWrapper(InputStream inputStream, List<InputStreamMonitor> monitors) {
    this.originalInputStream = inputStream;
    this.monitors = monitors;
  }

  public InputStream getOriginalInputStream() {
    return originalInputStream;
  }

  public void setOriginalInputStream(InputStream originalInputStream) {
    this.originalInputStream = originalInputStream;
  }

  public MonitorInputStreamWrapper addMonitor(InputStreamMonitor monitor) {
    if (monitors == null) {
      monitors = new ArrayList<>(1);
    }
    monitors.add(monitor);
    return this;
  }

  public MonitorInputStreamWrapper removeMonitor(InputStreamMonitor monitor) {
    if (monitors != null) {
      monitors.remove(monitor);
    }
    return this;
  }

  @Override
  public int read() throws IOException {
    int b = originalInputStream.read();
    if (monitors != null) {
      for (InputStreamMonitor monitor : monitors) {
        monitor.onRead(b);
      }
    }
    return b;
  }

  public List<InputStreamMonitor> getMonitors() {
    return monitors;
  }

  public void setMonitors(List<InputStreamMonitor> monitors) {
    this.monitors = monitors;
  }
}
