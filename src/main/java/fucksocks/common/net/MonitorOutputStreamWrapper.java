package fucksocks.common.net;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Youchao Feng
 * @version 1.0
 * @date Sep 21, 2015 2:34 PM
 */
public class MonitorOutputStreamWrapper extends OutputStream {

  private OutputStream originalOutputStream;

  private List<OutputStreamMonitor> monitors;

  public MonitorOutputStreamWrapper(OutputStream outputStream, OutputStreamMonitor... monitors) {
    this.originalOutputStream = outputStream;
    this.monitors = new ArrayList<>(monitors.length);
    Collections.addAll(this.monitors, monitors);
  }

  public MonitorOutputStreamWrapper(OutputStream outputStream, List<OutputStreamMonitor> monitors) {
    this.originalOutputStream = outputStream;
    this.monitors = monitors;
  }

  public MonitorOutputStreamWrapper addMonitor(OutputStreamMonitor monitor) {
    if (monitors == null) {
      monitors = new ArrayList<>(1);
    }
    monitors.add(monitor);
    return this;
  }

  public MonitorOutputStreamWrapper removeMonitor(OutputStreamMonitor monitor) {
    if (monitors != null) {
      monitors.remove(monitor);
    }
    return this;
  }

  public OutputStream getOriginalOutputStream() {
    return originalOutputStream;
  }

  public void setOriginalOutputStream(OutputStream originalOutputStream) {
    this.originalOutputStream = originalOutputStream;
  }

  @Override
  public void write(int b) throws IOException {
    if (monitors != null) {
      for (OutputStreamMonitor monitor : monitors) {
        monitor.onWrite(b);
      }
    }
    originalOutputStream.write(b);
  }

  public List<OutputStreamMonitor> getMonitors() {
    return monitors;
  }

  public void setMonitors(List<OutputStreamMonitor> monitors) {
    this.monitors = monitors;
  }
}
