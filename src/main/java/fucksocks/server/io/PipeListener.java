/*
 * Copyright 2015-2025 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package fucksocks.server.io;

/**
 * The class <code>PipeListener</code> represents a pipe listener.
 * <p>
 * You can add a {@link PipeListener} to a {@link Pipe} to monitor the {@link Pipe}.
 * </p>
 * 
 * @author Youchao Feng
 * @date May 3, 2015 1:37:10 AM
 * @version 1.0
 *
 */
public interface PipeListener {

  /**
   * This method will be called when the {@link Pipe} started.
   * 
   * @param pipe The started {@link Pipe} instance.
   */
  public void onStarted(Pipe pipe);

  /**
   * This method will be called when the {@link Pipe} stopped.
   * 
   * @param pipe The stopped {@link Pipe} instance.
   */
  public void onStoped(Pipe pipe);

  /**
   * This method will be called when the {@link Pipe} transfered data.
   * 
   * @param pipe {@link Pipe} instance.
   * @param buffer Data which is transfered.
   * @param bufferLength length of data.
   */
  public void onTransfered(Pipe pipe, byte[] buffer, int bufferLength);

}
