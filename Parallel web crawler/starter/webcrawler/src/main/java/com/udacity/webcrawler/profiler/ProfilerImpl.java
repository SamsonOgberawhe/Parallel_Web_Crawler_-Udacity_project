package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Objects;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
//This class will "wrap" the to-be-profiled objects in a dynamic proxy instance.

final class ProfilerImpl implements Profiler {

  private final Clock clock;
  private final ProfilingState state = new ProfilingState();
  private final ZonedDateTime startTime;

  @Inject
  ProfilerImpl(Clock clock) {
    this.clock = Objects.requireNonNull(clock);
    this.startTime = ZonedDateTime.now(clock);
  }

  @Profiled
  public boolean isClassProfiled(Class<?> klasse) throws IllegalArgumentException{
    Method[] methods = klasse.getDeclaredMethods();
    if (methods.length == 0) return false;
    for (Method method : methods) {
      if (method.getAnnotation(Profiled.class) != null) {
        return true;
      }
    }
    return false;
  }


  @Override
  public <T> T wrap(Class<T> klass, T delegate) throws IllegalArgumentException{

    Objects.requireNonNull(klass);

    // TODO: Use a dynamic proxy (java.lang.reflect.Proxy) to "wrap" the delegate in a
    //       ProfilingMethodInterceptor and return a dynamic proxy from this method.
    //       See https://docs.oracle.com/javase/10/docs/api/java/lang/reflect/Proxy.html.

    /*
    * InvocationHandler handler = new MyInvocationHandler(...);
     Foo f = (Foo) Proxy.newProxyInstance(Foo.class.getClassLoader(),
                                          new Class<?>[] { Foo.class },
                                          handler);*/

    if (!isClassProfiled(klass)) {
      throw new IllegalArgumentException(
              "No profiled method !!"
      );
    }

    ProfilingMethodInterceptor profilingMethodInterceptor = new ProfilingMethodInterceptor(
            this.clock,
            delegate,
            this.state,
            this.startTime
    );



    @SuppressWarnings("unchecked")
       Object proxy = (T) Proxy.newProxyInstance (
              ProfilerImpl.class.getClassLoader(),
              new Class[]{klass},
              profilingMethodInterceptor
      );





    return (T)proxy;

  }

  @Override
  public void writeData(Path path) throws IOException{
    // TODO: Write the ProfilingState data to the given file path. If a file already exists at that
    //       path, the new data should be appended to the existing file.
    Objects.requireNonNull(path);
    if (Files.notExists(path)){
      Files.createFile(path);
    }
    try{
      BufferedWriter writer = Files.newBufferedWriter(path);
      writeData(writer);
    }catch(IOException ioe){
      ioe.printStackTrace();
    }

  }


  @Override
  public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
  }

}
