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

package sockslib.test.quickstart;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The class <code>ArgumentsBuilder</code> is a tool class to build arguments as
 * <code>String[]</code>.
 *
 * @author Youchao Feng
 * @version 1.0
 * @date Oct 19, 2015 9:50 AM
 */
public final class ArgumentsBuilder {

  private List<String> arguments;

  private ArgumentsBuilder() {
    arguments = new ArrayList<>(10);
  }

  public static ArgumentsBuilder newBuilder() {
    return new ArgumentsBuilder();
  }

  public ArgumentsBuilder addArguments(String argsValue) {
    if (argsValue != null) {
      String[] args = argsValue.split("\\s+");
      for (String arg : args) {
        arguments.add(arg.trim());
      }
    }
    return this;
  }

  public ArgumentsBuilder addArgument(String arg) {
    arguments.add(checkNotNull(arg));
    return this;
  }

  public ArgumentsBuilder removeArgument(String arg) {
    arguments.remove(arg);
    return this;
  }

  public String[] build() {
    String[] args = new String[arguments.size()];
    args = arguments.toArray(args);
    return args;
  }

}
